package com.plusmobileapps.sample.anvilkmp

import com.plusmobileapps.sample.anvilkmp.di.AppComponent
import me.tatarka.inject.annotations.Provides
import platform.UIKit.UIApplication
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.reflect.KClass

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class IosAppComponent(
    @get:Provides val application: UIApplication,
) : AppComponent

/**
 * The `actual fun` will be generated for each iOS specific target. See [MergeComponent] for
 * more details.
 */
@MergeComponent.CreateComponent
expect fun KClass<IosAppComponent>.createIosAppComponent(
    application: UIApplication
): IosAppComponent