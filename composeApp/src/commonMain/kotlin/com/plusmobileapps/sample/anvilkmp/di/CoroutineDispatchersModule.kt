package com.plusmobileapps.sample.anvilkmp.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.coroutines.CoroutineContext

@ContributesTo(AppScope::class)
interface CoroutineDispatchersModule {
    @Provides
    @SingleIn(AppScope::class)
    fun ioDispatcher(): @Named("IO") CoroutineContext = Dispatchers.IO

    @Provides
    @SingleIn(AppScope::class)
    fun mainDispatcher(): @Named("MAIN") CoroutineContext = Dispatchers.Main

    @Provides
    @SingleIn(AppScope::class)
    fun defaultDispatcher(): @Named("DEFAULT") CoroutineContext = Dispatchers.Default

    @Provides
    @SingleIn(AppScope::class)
    fun unconfinedDispatcher(): @Named("UNCONFINED") CoroutineContext = Dispatchers.Unconfined
}