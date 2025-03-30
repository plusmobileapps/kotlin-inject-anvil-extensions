package com.plusmobileapps.sample.anvilkmp.blocs

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.plusmobileapps.kotlin.inject.decompose.runtime.ContributesAssistedFactory
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

interface RootBloc {
    val routerState: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Home(val bloc: HomeBloc) : Child()
    }

    interface Factory {
        fun create(context: ComponentContext): RootBloc
    }
}

@Inject
@ContributesAssistedFactory(assistedFactory = RootBloc.Factory::class)
class RootBlocImpl(
    @Assisted context: ComponentContext,
    private val homeBloc: HomeBloc.Factory,
) : RootBloc, ComponentContext by context {

    private val navigation = StackNavigation<Configuration>()

    private val stack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialStack = {
            listOf(Configuration.Home)
        },
        handleBackButton = true,
        key = "RootRouter",
        childFactory = ::createChild
    )


    override val routerState: Value<ChildStack<*, RootBloc.Child>> = stack

    private fun createChild(
        configuration: Configuration,
        componentContext: ComponentContext,
    ): RootBloc.Child =
        when (configuration) {
            Configuration.Home -> RootBloc.Child.Home(
                homeBloc.create(componentContext, ::onHomeOutput)
            )
        }

    private fun onHomeOutput(output: HomeBloc.Output) {
        when (output) {
            is HomeBloc.Output.NavigateToDetail -> TODO()
        }
    }

    @Serializable
    private sealed class Configuration {
        @Serializable
        data object Home : Configuration()
    }
}