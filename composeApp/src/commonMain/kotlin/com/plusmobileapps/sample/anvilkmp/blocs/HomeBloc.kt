package com.plusmobileapps.sample.anvilkmp.blocs

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.plusmobileapps.kotlin.inject.runtime.ContributesAssistedFactory
import com.plusmobileapps.sample.anvilkmp.Greeting
import com.plusmobileapps.sample.anvilkmp.data.Repository
import com.plusmobileapps.sample.anvilkmp.util.Consumer
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

interface HomeBloc {

    val models: Value<Model>

    data class Model(
        val greeting: Greeting,
    )

    sealed class Output {
        data object NavigateToDetail : Output()
    }

    interface Factory {
        fun create(
            context: ComponentContext,
            output: Consumer<Output>,
        ): HomeBloc
    }
}

@Inject
@ContributesAssistedFactory(
    scope = AppScope::class,
    assistedFactory = HomeBloc.Factory::class,
)
class HomeBlocImpl(
    @Assisted context: ComponentContext,
    @Assisted private val output: Consumer<HomeBloc.Output>,
    repository: Repository,
) : HomeBloc, ComponentContext by context {

    override val models: Value<HomeBloc.Model> = MutableValue(HomeBloc.Model(Greeting()))

    init {

    }
}