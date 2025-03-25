package com.plusmobileapps.sample.anvilkmp.di

import com.plusmobileapps.sample.anvilkmp.blocs.root.RootBloc


interface AppComponent {
    val rootBlocFactory: RootBloc.Factory
}

