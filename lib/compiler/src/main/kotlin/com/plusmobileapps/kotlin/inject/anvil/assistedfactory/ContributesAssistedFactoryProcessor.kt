@file:OptIn(KspExperimental::class)

package com.plusmobileapps.kotlin.inject.anvil.assistedfactory

import com.google.auto.service.AutoService
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.plusmobileapps.kotlin.inject.anvil.assistedfactory.util.addOriginAnnotation
import com.plusmobileapps.kotlin.inject.anvil.assistedfactory.util.findAnnotations
import com.plusmobileapps.kotlin.inject.anvil.assistedfactory.util.innerClassNames
import com.plusmobileapps.kotlin.inject.anvil.assistedfactory.util.requireContainingFile
import com.plusmobileapps.kotlin.inject.anvil.assistedfactory.util.requireQualifiedName
import com.plusmobileapps.kotlin.inject.anvil.assistedfactory.util.safeClassName
import com.plusmobileapps.kotlin.inject.runtime.ContributesAssistedFactory
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import kotlin.reflect.KClass

const val LOOKUP_PACKAGE = "com.plusmobileapps.kotlin.inject.anvil"

internal class ContributesAssistedFactoryProcessor(
    private val codeGenerator: CodeGenerator,
    val logger: KSPLogger,
) : SymbolProcessor {

    @AutoService(SymbolProcessorProvider::class)
    @Suppress("unused")
    class Provider : SymbolProcessorProvider {
        override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
            return ContributesAssistedFactoryProcessor(
                codeGenerator = environment.codeGenerator,
                logger = environment.logger,
            )
        }
    }

    private val anyFqName = Any::class.requireQualifiedName()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver
            .getSymbolsWithAnnotation(ContributesAssistedFactory::class.requireQualifiedName())
            .filterIsInstance<KSClassDeclaration>()
            .onEach {
                validate(annotatedClass = it)
            }
            .forEach {
                generateComponentInterface(it)
            }

        return emptyList()
    }

    private fun validate(annotatedClass: KSClassDeclaration) {
        require(annotatedClass.isPublic()) {
            """${annotatedClass.toClassName()} must be public.
                        | kotlin-inject does not support components that are not public."""
                .trimMargin()
        }
        require(annotatedClass.isInjected()) {
            """${annotatedClass.toClassName()} must be annotated with @Inject."""
        }
    }

    @OptIn(KspExperimental::class)
    private fun KSAnnotated.isInjected(): Boolean {
        return isAnnotationPresent(Inject::class)
    }


    @Suppress("LongMethod")
    private fun generateComponentInterface(clazz: KSClassDeclaration) {
        val componentClassName = ClassName(LOOKUP_PACKAGE, clazz.safeClassName(logger))

        val constructor = clazz.getConstructors().firstOrNull { constructor ->
            constructor.parameters.any { it.isAnnotationPresent(Assisted::class) }
        } ?: throw IllegalArgumentException(
            "No constructor with @Assisted found in ${clazz.simpleName.asString()}",
        )
        val constructorParameters = constructor.parameters

        val realAssistedFactory: LambdaTypeName = createRealAssistedFactory(
            constructorParameters = constructorParameters,
            clazz = clazz,
        )

        val annotations = clazz.findAnnotationsAtLeastOne(ContributesAssistedFactory::class)
        checkNoDuplicateBoundTypes(clazz, annotations)

        val assistedFactoryType = assistedFactoryFromAnnotation(annotations.first())
        checkIsSingleMethodInterface(assistedFactoryType)

        val generatedFunction = annotations.first().let {
            GeneratedFunction(
                boundType = getSingleMethodReturnType(assistedFactoryType)!!,
                assistedFactory = assistedFactoryFromAnnotation(it),
            )
        }

        val scope = getScope(clazz)

        val fileSpec = FileSpec.builder(componentClassName)
            .apply {
                addImport(
                    generatedFunction.bindingMethodReturnType.packageName,
                    generatedFunction.bindingMethodReturnType.simpleName,
                )
                addImport(
                    generatedFunction.assistedFactoryReturnType.packageName,
                    generatedFunction.assistedFactoryReturnType.simpleNames.joinToString("."),
                )
            }
            .addType(
                createComponent(
                    componentClassName = componentClassName,
                    clazz = clazz,
                    function = generatedFunction,
                    realAssistedFactory = realAssistedFactory,
                    constructorParameters = constructorParameters,
                    scope = scope,
                ),
            )
            .build()

        fileSpec.writeTo(codeGenerator, aggregating = false)
    }

    private fun createComponent(
        componentClassName: ClassName,
        clazz: KSClassDeclaration,
        function: GeneratedFunction,
        realAssistedFactory: LambdaTypeName,
        constructorParameters: List<KSValueParameter>,
        scope: TypeName,
    ): TypeSpec = TypeSpec
        .interfaceBuilder(componentClassName)
        .addOriginatingKSFile(clazz.requireContainingFile(logger))
        .addOriginAnnotation(clazz)
        .addAnnotation(
            AnnotationSpec.builder(ContributesTo::class)
                .addMember("%T::class", scope)
                .build()
        )
        .addFunction(
            FunSpec
                .builder(
                    "provide${clazz.innerClassNames(logger)}" +
                            function.bindingMethodReturnType.simpleName,
                )
                .addAnnotation(Provides::class)
                .apply {
                    addParameter(
                        ParameterSpec.builder(
                            "realFactory",
                            realAssistedFactory,
                        ).build(),
                    )
                    addStatement(
                        """
                        return Default${function.assistedFactoryReturnType.simpleName}(
                            realFactory = realFactory
                        )
                        """.trimIndent(),
                    )
                }
                .returns(function.assistedFactoryReturnType)
                .build(),
        )
        .addType(
            createDefaultAssistedFactory(
                realAssistedFactory = realAssistedFactory,
                boundAssistedFactory = function.assistedFactoryReturnType,
                bindingMethodReturnType = function.bindingMethodReturnType,
                assistedFactoryFunctionName = function.assistedFactoryFunctionName,
                constructorParameters = constructorParameters,
            ),
        )
        .build()

    /**
     * Create a lambda to represent the assisted factory provided by kotlin-inject.
     *
     * When marking an injectable class with @Assisted, kotlin-inject will generate a binding in
     * lambda form to inject something that can create an instance.
     */
    private fun createRealAssistedFactory(
        constructorParameters: List<KSValueParameter>,
        clazz: KSClassDeclaration,
    ): LambdaTypeName = LambdaTypeName.get(
        parameters = constructorParameters
            .filter { it.isAnnotationPresent(Assisted::class) }
            .map { it.type.toTypeName() }
            .toTypedArray(),
        returnType = clazz.toClassName(),
    )

    /**
     * Create a default assisted factory concreate class that implements the assisted factory
     * interface provided in the @ContributesAssistedFactory annotation.
     */
    private fun createDefaultAssistedFactory(
        realAssistedFactory: LambdaTypeName,
        boundAssistedFactory: ClassName,
        bindingMethodReturnType: ClassName,
        assistedFactoryFunctionName: String,
        constructorParameters: List<KSValueParameter>,
    ): TypeSpec {
        return TypeSpec.classBuilder("Default${boundAssistedFactory.simpleName}")
            .addModifiers(KModifier.PRIVATE)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(
                        ParameterSpec.builder("realFactory", realAssistedFactory).build(),
                    )
                    .build(),
            )
            .addProperty(
                PropertySpec.builder(
                    "realFactory",
                    realAssistedFactory,
                )
                    .initializer("realFactory")
                    .addModifiers(KModifier.PRIVATE)
                    .build(),
            )
            .addSuperinterface(boundAssistedFactory)
            .addFunction(
                FunSpec.builder(assistedFactoryFunctionName)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameters(
                        constructorParameters
                            .filter { it.isAnnotationPresent(Assisted::class) }
                            .map { param ->
                                val paramName = param.name!!.asString()
                                val paramType = param.type.resolve().toTypeName()
                                ParameterSpec.builder(paramName, paramType).build()
                            },
                    )
                    .addStatement(
                        "return realFactory(${
                            constructorParameters.filter {
                                it.isAnnotationPresent(
                                    Assisted::class,
                                )
                            }.joinToString { it.name!!.asString() }
                        })",
                    )
                    .returns(bindingMethodReturnType)
                    .build(),

                )
            .build()
    }

    private fun checkNoDuplicateBoundTypes(
        clazz: KSClassDeclaration,
        annotations: List<KSAnnotation>,
    ) {
        annotations
            .mapNotNull { boundTypeFromAssistedFactory(it) }
            .map { it.declaration.requireQualifiedName(logger) }
            .takeIf { it.isNotEmpty() }
            ?.reduce { previous, next ->
                check(previous != next) {
                    "The same type should not be contributed twice: $next.".also {
                        logger.error(it, clazz)
                    }
                }

                previous
            }
    }

    private fun boundTypeFromAssistedFactory(annotation: KSAnnotation): KSType? {
        return annotation.arguments.firstOrNull { it.name?.asString() == "boundType" }
            ?.let { it.value as? KSType }
            ?.takeIf {
                it.declaration.requireQualifiedName(logger) != Unit::class.requireQualifiedName()
            }
    }

    private fun assistedFactoryFromAnnotation(annotation: KSAnnotation): KSType {
        return annotation.arguments.firstOrNull { it.name?.asString() == "assistedFactory" }
            ?.let { it.value as? KSType }
            ?.takeIf {
                it.declaration.requireQualifiedName(logger) != Unit::class.requireQualifiedName()
            } ?: throw IllegalArgumentException(
            "Assisted factory type must be specified in " +
                    "the @ContributesAssistedFactory annotation.",
        )
    }

    private fun KSClassDeclaration.getAssistedFactoryInterfaceFunctions() = getAllFunctions()
        .filterNot {
            val simpleName = it.simpleName.asString()
            simpleName == "equals" || simpleName == "hashCode" || simpleName == "toString"
        }
        .toList()

    private fun checkIsSingleMethodInterface(type: KSType) {
        val declaration = type.declaration
        if (declaration is KSClassDeclaration && declaration.classKind == ClassKind.INTERFACE) {
            val methods = declaration.getAssistedFactoryInterfaceFunctions()
            check(methods.size == 1) {
                "The assisted factory must have exactly one method."
            }
        } else {
            throw IllegalArgumentException("The assisted factory must be an interface.")
        }
    }

    private fun getSingleMethodReturnType(type: KSType): KSType? {
        val declaration = type.declaration
        if (declaration is KSClassDeclaration) {
            val methods = declaration.getAssistedFactoryInterfaceFunctions()
            if (methods.size == 1) {
                return methods[0].returnType?.resolve()
            }
        }
        return null
    }

    @Suppress("ReturnCount")
    private fun boundType(
        clazz: KSClassDeclaration,
        annotation: KSAnnotation,
    ): KSType {
        boundTypeFromAssistedFactory(annotation)?.let { return it }

        // The bound type is not defined in the annotation, let's inspect the super types.
        val superTypes = clazz.superTypes
            .map { it.resolve() }
            .filter { it.declaration.requireQualifiedName(logger) != anyFqName }
            .toList()

        when (superTypes.size) {
            0 -> {
                val message = "The bound type could not be determined for " +
                        "${clazz.simpleName.asString()}. There are no super types."
                logger.error(message, clazz)
                throw IllegalArgumentException(message)
            }

            1 -> {
                return superTypes.single()
            }

            else -> {
                val message = "The bound type could not be determined for " +
                        "${clazz.simpleName.asString()}. There are multiple super types: " +
                        superTypes.joinToString { it.declaration.simpleName.asString() } +
                        "."
                logger.error(message, clazz)
                throw IllegalArgumentException(message)
            }
        }
    }

    private fun KSClassDeclaration.findAnnotationsAtLeastOne(
        annotation: KClass<out Annotation>,
    ): List<KSAnnotation> {
        return findAnnotations(annotation, logger).also {
            check(it.isNotEmpty()) {
                "Couldn't find the @${annotation.simpleName} annotation for $this.".also {
                    logger.error(it, this)
                }
            }
        }
    }

    private inner class GeneratedFunction(
        boundType: KSType,
        assistedFactory: KSType,
    ) {
        val bindingMethodReturnType: ClassName by lazy {
            boundType.toClassName()
        }
        val assistedFactoryReturnType: ClassName by lazy {
            if (assistedFactory.declaration.parentDeclaration != null) {
                val parentClassName =
                    buildClassName(assistedFactory.declaration as KSClassDeclaration)
                ClassName(parentClassName.packageName, parentClassName.simpleNames)
            } else {
                assistedFactory.toClassName()
            }
        }

        val assistedFactoryFunctionName: String by lazy {
            val classDeclaration = assistedFactory.declaration as? KSClassDeclaration
                ?: throw IllegalArgumentException(
                    "Assisted factory type must be a class.",
                )
            classDeclaration.getAllFunctions()
                .map(KSFunctionDeclaration::simpleName)
                .map { it.asString() }
                .first()
        }

        private fun buildClassName(declaration: KSClassDeclaration): ClassName {
            val parent = declaration.parentDeclaration
            return if (parent is KSClassDeclaration) {
                val parentClassName = buildClassName(parent)
                ClassName(
                    parentClassName.packageName,
                    parentClassName.simpleNames + declaration.simpleName.asString(),
                )
            } else {
                ClassName(declaration.packageName.asString(), declaration.simpleName.asString())
            }
        }
    }

    private fun getScope(element: KSClassDeclaration): TypeName {
        // There is a simpler way to do this using the experimental getAnnotationsByType, however it doesn't
        // work when attempting to retrieve the type of the scope argument if it's not on this project's classpath.
        val annotation = element.annotations
            .first { annotation ->
                val clazz = ContributesAssistedFactory::class
                annotation.shortName.asString() == clazz.simpleName
                        && annotation.annotationType.resolve().declaration.qualifiedName?.asString() == clazz.qualifiedName
            }
        val scopeArgument = annotation.arguments
            .first { argument ->
                argument.name?.asString() == ContributesAssistedFactory::scope.name
            }
            .value as KSType
        return scopeArgument.toTypeName()
    }
}