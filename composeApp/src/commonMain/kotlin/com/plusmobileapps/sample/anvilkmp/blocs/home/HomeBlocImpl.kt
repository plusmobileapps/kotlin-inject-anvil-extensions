package com.plusmobileapps.sample.anvilkmp.blocs.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.plusmobileapps.sample.anvilkmp.Greeting
import com.plusmobileapps.sample.anvilkmp.data.Repository
import com.plusmobileapps.sample.anvilkmp.util.Consumer
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.AssistedFactory
import me.tatarka.inject.annotations.Inject

@AssistedFactory
interface HomeBlocImplFactory {
    fun create(
        context: ComponentContext,
        output: Consumer<HomeBloc.Output>,
    ): HomeBlocImpl
}

@Inject
class HomeBlocImpl(
    @Assisted context: ComponentContext,
    @Assisted private val output: Consumer<HomeBloc.Output>,
    repository: Repository,
) : HomeBloc, ComponentContext by context {

    override val models: Value<HomeBloc.Model> = MutableValue(HomeBloc.Model(Greeting()))

    init {

    }
}