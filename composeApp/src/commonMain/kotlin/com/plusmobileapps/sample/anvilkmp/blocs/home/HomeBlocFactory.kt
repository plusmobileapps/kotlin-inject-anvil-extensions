package com.plusmobileapps.sample.anvilkmp.blocs.home

import com.arkivanov.decompose.ComponentContext
import com.plusmobileapps.sample.anvilkmp.util.Consumer
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

interface HomeBlocFactory {
    fun create(
        context: ComponentContext,
        output: Consumer<HomeBloc.Output>,
    ): HomeBloc
}

@Inject
@ContributesBinding(AppScope::class, boundType = HomeBlocFactory::class)
@SingleIn(AppScope::class)
class DefaultHomeBlocFactory(
    private val realFactory: HomeBlocImplFactory,
) : HomeBlocFactory {
    override fun create(
        context: ComponentContext,
        output: Consumer<HomeBloc.Output>
    ): HomeBloc = realFactory.create(context, output)
}