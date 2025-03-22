package com.plusmobileapps.sample.anvilkmp.blocs.root

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn


interface RootBlocFactory {
    fun create(
        context: ComponentContext,
    ): RootBloc
}

@Inject
@ContributesBinding(AppScope::class, boundType = RootBlocFactory::class)
@SingleIn(AppScope::class)
class DefaultRootBlocFactory(
    val realFactory: RootBlocImplFactory,
) : RootBlocFactory {
    override fun create(
        context: ComponentContext,
    ): RootBloc = realFactory.create(context)
}