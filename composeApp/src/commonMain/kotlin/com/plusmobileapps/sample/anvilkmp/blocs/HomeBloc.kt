package com.plusmobileapps.sample.anvilkmp.blocs

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.plusmobileapps.sample.anvilkmp.Greeting
import com.plusmobileapps.sample.anvilkmp.data.Repository
import com.plusmobileapps.sample.anvilkmp.util.Consumer
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.AssistedFactory
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

interface HomeBloc {

    val models: Value<Model>

    data class Model(
        val greeting: Greeting,
    )

    sealed class Output {
        data object NavigateToDetail : Output()
    }
}

@ContributesTo(AppScope::class)
@SingleIn(AppScope::class)
interface HomeBlocComponent {
    @Provides
    fun providesHomeBlocFactory(
        realFactory: HomeBlocImplFactory,
    ): HomeBlocFactory = object : HomeBlocFactory {
        override fun create(
            context: ComponentContext,
            output: Consumer<HomeBloc.Output>
        ): HomeBloc = realFactory.create(context, output)
    }
}

interface HomeBlocFactory {
    fun create(
        context: ComponentContext,
        output: Consumer<HomeBloc.Output>,
    ): HomeBloc
}

@AssistedFactory
interface HomeBlocImplFactory {
    fun create(
        context: ComponentContext,
        output: Consumer<HomeBloc.Output>,
    ): HomeBlocImpl
}

@Inject
class HomeBlocImpl(
    @Assisted context: ComponentContext,
    @Assisted private val output: Consumer<HomeBloc.Output>,
    repository: Repository,
) : HomeBloc, ComponentContext by context {

    override val models: Value<HomeBloc.Model> = MutableValue(HomeBloc.Model(Greeting()))

    init {

    }
}