package com.plusmobileapps.sample.anvilkmp.di

import com.arkivanov.decompose.ComponentContext
import com.plusmobileapps.sample.anvilkmp.blocs.HomeBloc
import com.plusmobileapps.sample.anvilkmp.blocs.HomeBlocImpl
import com.plusmobileapps.sample.anvilkmp.blocs.RootBloc
import com.plusmobileapps.sample.anvilkmp.blocs.RootBlocImpl
import com.plusmobileapps.sample.anvilkmp.data.Repository
import com.plusmobileapps.sample.anvilkmp.util.Consumer
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
interface AppComponent {
    abstract val rootBlocFactory: (ComponentContext) -> RootBloc
    abstract val repository: Repository

    @Provides
    fun provideRootBloc(
        @Assisted context: ComponentContext,
        homeBloc: (context: ComponentContext, output: Consumer<HomeBloc.Output>) -> HomeBloc,
    ): RootBloc = RootBlocImpl(context = context, homeBloc = homeBloc)

    @Provides
    fun providesHomeBloc(
        repository: Repository,
        @Assisted context: ComponentContext,
        @Assisted output: Consumer<HomeBloc.Output>,
    ): HomeBloc = HomeBlocImpl(repository = repository, context = context, output = output)
}

