package com.plusmobileapps.sample.anvilkmp.blocs.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.plusmobileapps.sample.anvilkmp.Greeting
import com.plusmobileapps.sample.anvilkmp.util.Consumer

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
            output: Consumer<HomeBloc.Output>,
        ): HomeBloc
    }
}