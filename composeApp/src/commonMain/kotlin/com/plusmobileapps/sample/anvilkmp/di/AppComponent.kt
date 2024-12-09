package com.plusmobileapps.sample.anvilkmp.di

import com.arkivanov.decompose.ComponentContext
import com.plusmobileapps.sample.anvilkmp.blocs.HomeBloc
import com.plusmobileapps.sample.anvilkmp.blocs.HomeBlocFactory
import com.plusmobileapps.sample.anvilkmp.blocs.HomeBlocImpl
import com.plusmobileapps.sample.anvilkmp.blocs.RootBloc
import com.plusmobileapps.sample.anvilkmp.blocs.RootBlocFactory
import com.plusmobileapps.sample.anvilkmp.blocs.RootBlocImpl
import com.plusmobileapps.sample.anvilkmp.data.Repository
import com.plusmobileapps.sample.anvilkmp.util.Consumer
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Provides

interface AppComponent: CoroutineDispatchersModule {
    abstract val rootBlocFactory: RootBlocFactory

    @Provides
    fun provideRootBloc(
        @Assisted context: ComponentContext,
        homeBloc: HomeBlocFactory,
    ): RootBloc = RootBlocImpl(context = context, homeBloc = homeBloc)

    @Provides
    fun providesHomeBloc(
        repository: Repository,
        @Assisted context: ComponentContext,
        @Assisted output: Consumer<HomeBloc.Output>,
    ): HomeBloc = HomeBlocImpl(repository = repository, context = context, output = output)
}

