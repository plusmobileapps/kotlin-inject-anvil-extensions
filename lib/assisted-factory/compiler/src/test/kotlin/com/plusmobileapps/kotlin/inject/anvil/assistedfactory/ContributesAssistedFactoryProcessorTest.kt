@file:OptIn(ExperimentalCompilerApi::class)

package com.plusmobileapps.kotlin.inject.anvil.assistedfactory

import com.plusmobileapps.kotlin.inject.anvil.extensions.assistedfactory.compiler.ContributesAssistedFactoryProcessor
import com.plusmobileapps.kotlin.inject.anvil.extensions.assistedfactory.compiler.LOOKUP_PACKAGE
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.descriptors.runtime.structure.primitiveByWrapper
import org.junit.jupiter.api.Test
import software.amazon.lastmile.kotlin.inject.anvil.internal.Origin


class ContributesAssistedFactoryProcessorTest {
    @Test
    fun `annotated class must extend ViewModel`() {
        compile(
            """
            package software.amazon.test
    
            import com.plusmobileapps.kotlin.inject.anvil.extensions.assistedfactory.runtime.ContributesAssistedFactory
            import me.tatarka.inject.annotations.Inject
            import me.tatarka.inject.annotations.Assisted
            interface Base
            @Inject
            @ContributesAssistedFactory(
                scope = Unit::class,
                assistedFactory = BaseFactory::class,
            )
            class Impl(
                @Assisted val id: String,
            ) : Base   
            interface BaseFactory {
                fun create(id: String): Base
            }
            """
        ) {
            val impl = impl
            val component = impl.generatedComponent

            component.packageName shouldBeEqual  LOOKUP_PACKAGE
            component.origin shouldBeEqual impl

            val method = component.declaredMethods.single()
            component.declaredMethods shouldHaveSize 1
            method.returnType shouldBeEqual baseFactory
            method.name shouldBeEqual "provideImplBase"

            val parameter = method.parameters.single()
            method.parameters.size shouldBeEqual 1
            parameter.type shouldBeEqual realAssistedFactory
        }
    }

    @Test
    fun `a component interface is generated with contributes assisted factory as nested interface`() {
        compile(
            """
            package software.amazon.test
    
            import com.plusmobileapps.kotlin.inject.anvil.extensions.assistedfactory.runtime.ContributesAssistedFactory
            import me.tatarka.inject.annotations.Inject
            import me.tatarka.inject.annotations.Assisted

            interface Base {
                interface Factory {
                    fun create(id: String): Base
                }
            }

            @Inject
            @ContributesAssistedFactory(
                scope = Unit::class,
                assistedFactory = Base.Factory::class,
            )
            class Impl(
                @Assisted val id: String,
            ) : Base
            """,
        ) {
            val component = impl.generatedComponent

            component.packageName shouldBeEqual LOOKUP_PACKAGE
            component.origin shouldBeEqual impl

            val method = component.declaredMethods.single()
            component.declaredMethods shouldHaveSize 1
            method.returnType shouldBeEqual nestedBaseFactory
            method.name shouldBeEqual "provideImplBase"

            val parameter = method.parameters.single()
            method.parameters.size shouldBeEqual 1
            parameter.type shouldBeEqual realAssistedFactory
        }
    }

    @Test
    fun `the kotlin-inject component contains assisted factory binding`() {
        compile(
            """
            package software.amazon.test
    
            import com.plusmobileapps.kotlin.inject.anvil.extensions.assistedfactory.runtime.ContributesAssistedFactory
            import me.tatarka.inject.annotations.Inject
            import me.tatarka.inject.annotations.Assisted
            import software.amazon.lastmile.kotlin.inject.anvil.AppScope
            import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
            import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

            interface Base

            @Inject
            @ContributesAssistedFactory(
                scope = AppScope::class,
                assistedFactory = BaseFactory::class,
            )
            class Impl(
                @Assisted val id: String,
            ) : Base   

            interface BaseFactory {
                fun create(id: String): Base
            }

            @MergeComponent(AppScope::class)
            @SingleIn(AppScope::class)
            interface ComponentInterface {
                val baseFactory: BaseFactory
            }
            """,
        ) {
            // TODO
//            val component = componentInterface.kotlinInjectComponent.newComponent<Any>()
//
//            val implValue = component::class.java.methods
//                .single { it.name == "provideImplBase" }
//                .invoke(component, { id: String -> })
//
//            defaultBaseFactory.isInstance(implValue).shouldBeTrue()
        }
    }


    private val JvmCompilationResult.baseFactory: Class<*>
        get() = classLoader.loadClass("software.amazon.test.BaseFactory")

    private val JvmCompilationResult.nestedBaseFactory: Class<*>
        get() = classLoader.loadClass("software.amazon.test.Base${'$'}Factory")

    private val JvmCompilationResult.defaultBaseFactory: Class<*>
        get() = classLoader.loadClass(
            "amazon.lastmile.inject.SoftwareAmazonTestImpl${'$'}DefaultBaseFactory",
        )

    private val JvmCompilationResult.impl: Class<*>
        get() = classLoader.loadClass("software.amazon.test.Impl")

    private val JvmCompilationResult.realAssistedFactory: Class<*>
        get() = classLoader.loadClass("kotlin.jvm.functions.Function1")

    private val JvmCompilationResult.componentInterface: Class<*>
        get() = classLoader.loadClass("software.amazon.test.ComponentInterface")

    private val Class<*>.kotlinInjectComponent: Class<*>
        get() = classLoader.loadClass(
            "$packageName.KotlinInject" +
                    canonicalName.substring(packageName.length + 1).replace(".", ""),
        )

    private val Class<*>.generatedComponent: Class<*>
        get() = classLoader.loadClass(
            "$LOOKUP_PACKAGE." +
                    canonicalName.split(".").joinToString(separator = "") { it.capitalize() },
        )

    private val Class<*>.origin: Class<*>
        get() = getAnnotation(Origin::class.java).value.java

    private fun compile(@Language("kotlin") contents: String, block: JvmCompilationResult.() -> Unit) {
        compile(SourceFile.kotlin("Source.kt", contents)) {
            block(this)
        }
    }

    private fun <T : Any> Class<*>.newComponent(vararg arguments: Any): T {
        @Suppress("UNCHECKED_CAST", "SpreadOperator")
        return classLoader.loadClass("$packageName.Inject$simpleName")
            .getDeclaredConstructor(
                *arguments.map { arg ->
                    arg::class.java.primitiveByWrapper ?: arg::class.java
                }.toTypedArray(),
            )
            .newInstance(*arguments) as T
    }

    private fun compile(vararg sources: SourceFile, block: JvmCompilationResult.() -> Unit) {
        KotlinCompilation().run {
            inheritClassPath = true
            allWarningsAsErrors = true
            verbose = false
            messageOutputStream = System.out
            this.sources = sources.toList()
            configureKsp(useKsp2 = true) {
                languageVersion = "2.0"
                symbolProcessorProviders += ContributesAssistedFactoryProcessor.Provider()
                allWarningsAsErrors = true
            }
            compile()
        }.run {
            block(this)
        }
    }
}