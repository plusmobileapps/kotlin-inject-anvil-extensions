package com.plusmobileapps.kotlin.inject.anvil.decompose.compiler

import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.plusmobileapps.kotlin.inject.decompose.runtime.ContributesBloc
import com.squareup.kotlinpoet.ClassName

private const val LOOKUP_PACKAGE = "com.plusmobileapps.kotlin.inject.decompose"

class ContributesBlocSymbolProvider(
    val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver
            .getSymbolsWithAnnotation(ContributesBloc::class)
            .filterIsInstance<KSClassDeclaration>()
            .onEach {
                require(it.isPublic()) {
                    "All annotated classes must be public: ${it.qualifiedName}"
                }
            }
            .forEach { generateComponentInterface(it) }
        return emptyList() // TODO
    }

    private fun generateComponentInterface(clazz: KSClassDeclaration) {
        val componentClassName = ClassName(LOOKUP_PACKAGE, clazz.safeClassName)
    }

    /**
     * Return `software.amazon.Test` into `SoftwareAmazonTest`.
     */
    val KSClassDeclaration.safeClassName: String
        get() = qualifiedName!!.asString()
            .split(".")
            .joinToString(separator = "") { it.capitalize() }

    private fun checkNoDuplicateBoundTypes(
        clazz: KSClassDeclaration,
        annotations: List<KSAnnotation>,
    ) {
        annotations
            .mapNotNull { boundTypeFromAnnotation(it) }
            .map { it.declaration.requireQualifiedName() }
            .takeIf { it.isNotEmpty() }
            ?.reduce { previous, next ->
                check(previous != next, clazz) {
                    "The same type should not be contributed twice: $next."
                }

                previous
            }
    }


}