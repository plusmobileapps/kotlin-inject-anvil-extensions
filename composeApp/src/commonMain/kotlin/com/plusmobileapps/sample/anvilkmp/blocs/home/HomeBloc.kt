package com.plusmobileapps.sample.anvilkmp.blocs.home

import com.arkivanov.decompose.value.Value
import com.plusmobileapps.sample.anvilkmp.Greeting

interface HomeBloc {

    val models: Value<Model>

    data class Model(
        val greeting: Greeting,
    )

    sealed class Output {
        data object NavigateToDetail : Output()
    }
}