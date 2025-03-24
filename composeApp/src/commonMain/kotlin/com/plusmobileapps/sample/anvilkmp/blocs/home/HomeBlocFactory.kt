package com.plusmobileapps.sample.anvilkmp.blocs.home

import com.arkivanov.decompose.ComponentContext
import com.plusmobileapps.sample.anvilkmp.util.Consumer

interface HomeBlocFactory {
    fun create(
        context: ComponentContext,
        output: Consumer<HomeBloc.Output>,
    ): HomeBloc
}