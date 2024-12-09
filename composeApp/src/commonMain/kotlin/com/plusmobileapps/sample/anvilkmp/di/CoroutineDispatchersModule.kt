package com.plusmobileapps.sample.anvilkmp.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Qualifier
import kotlin.coroutines.CoroutineContext

@Qualifier
@Target(
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE
)
annotation class IO

@Qualifier
@Target(
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE
)
annotation class Main

@Qualifier
@Target(
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE
)
annotation class Default

@Qualifier
@Target(
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE
)
annotation class Unconfined

interface CoroutineDispatchersModule {
    @Provides
    fun ioDispatcher(): @IO CoroutineContext =
        Dispatchers.IO

    @Provides
    fun mainDispatcher(): @Main CoroutineContext =
        Dispatchers.Main

    @Provides
    fun defaultDispatcher(): @Default CoroutineContext =
        Dispatchers.Default

    @Provides
    fun unconfinedDispatcher(): @Unconfined CoroutineContext =
        Dispatchers.Unconfined
}