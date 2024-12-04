package com.plusmobileapps.sample.anvilkmp.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.coroutines.CoroutineContext

data object CoroutineConstants {
    const val IO = "IO"
    const val MAIN = "MAIN"
    const val DEFAULT = "DEFAULT"
    const val UNCONFINED = "UNCONFINED"
}

@ContributesTo(AppScope::class)
interface CoroutineDispatchersModule {
    @Provides
    @SingleIn(AppScope::class)
    fun ioDispatcher(): @Named(CoroutineConstants.IO) CoroutineContext =
        Dispatchers.IO

    @Provides
    @SingleIn(AppScope::class)
    fun mainDispatcher(): @Named(CoroutineConstants.MAIN) CoroutineContext =
        Dispatchers.Main

    @Provides
    @SingleIn(AppScope::class)
    fun defaultDispatcher(): @Named(CoroutineConstants.DEFAULT) CoroutineContext =
        Dispatchers.Default

    @Provides
    @SingleIn(AppScope::class)
    fun unconfinedDispatcher(): @Named(CoroutineConstants.UNCONFINED) CoroutineContext =
        Dispatchers.Unconfined
}