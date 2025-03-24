package com.plusmobileapps.sample.anvilkmp.blocs.root

import com.arkivanov.decompose.ComponentContext


interface RootBlocFactory {
    fun create(
        context: ComponentContext,
    ): RootBloc
}