package com.plusmobileapps.sample.anvilkmp.blocs

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.plusmobileapps.sample.anvilkmp.Greeting
import com.plusmobileapps.sample.anvilkmp.data.Repository
import com.plusmobileapps.sample.anvilkmp.util.Consumer

interface HomeBloc {

    val models: Value<Model>

    data class Model(
        val greeting: Greeting,
    )

    sealed class Output {
        data object NavigateToDetail : Output()
    }
}

typealias HomeBlocFactory = (context: ComponentContext, output: Consumer<HomeBloc.Output>) -> HomeBloc

class HomeBlocImpl(
    repository: Repository,
    context: ComponentContext,
    private val output: Consumer<HomeBloc.Output>,
) : HomeBloc, ComponentContext by context {

    override val models: Value<HomeBloc.Model> = MutableValue(HomeBloc.Model(Greeting()))

    init {

    }
}