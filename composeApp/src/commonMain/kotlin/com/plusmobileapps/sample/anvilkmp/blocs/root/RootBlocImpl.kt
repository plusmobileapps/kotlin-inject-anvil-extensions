package com.plusmobileapps.sample.anvilkmp.blocs.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.plusmobileapps.sample.anvilkmp.blocs.home.HomeBloc
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesAssistedFactory

@Inject
@ContributesAssistedFactory(
    scope = AppScope::class,
    assistedFactory = RootBloc.Factory::class,
)
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