package com.plusmobileapps.sample.anvilkmp.di

import com.plusmobileapps.sample.anvilkmp.blocs.RootBloc

interface AppComponent {
    abstract val rootBlocFactory: RootBloc.Factory
}

