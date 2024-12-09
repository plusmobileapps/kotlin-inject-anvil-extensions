package com.plusmobileapps.sample.anvilkmp.di

import me.tatarka.inject.annotations.Qualifier

@Qualifier
@Target(
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE,
    AnnotationTarget.CLASS
)
annotation class Singleton