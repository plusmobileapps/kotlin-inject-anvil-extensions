package com.plusmobileapps.kotlin.inject.decompose.runtime

import software.amazon.lastmile.kotlin.inject.anvil.extend.ContributingAnnotation
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@ContributingAnnotation
annotation class ContributesBloc(
    val scope: KClass<*>,
)