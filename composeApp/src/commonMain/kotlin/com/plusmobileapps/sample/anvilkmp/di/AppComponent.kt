package com.plusmobileapps.sample.anvilkmp.di

import com.plusmobileapps.sample.anvilkmp.blocs.root.RootBlocFactory

interface AppComponent {
    abstract val rootBlocFactory: RootBlocFactory
}

