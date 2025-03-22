package com.plusmobileapps.sample.anvilkmp.blocs.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.plusmobileapps.sample.anvilkmp.blocs.home.HomeBloc

interface RootBloc {
    val routerState: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Home(val bloc: HomeBloc) : Child()
    }
}