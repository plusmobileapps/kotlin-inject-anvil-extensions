package com.plusmobileapps.sample.anvilkmp

import com.plusmobileapps.sample.anvilkmp.di.AppComponent
import me.tatarka.inject.annotations.KmpComponentCreate
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class IosAppComponent : AppComponent {

    companion object
}

@KmpComponentCreate
expect fun IosAppComponent.Companion.createComponent(): IosAppComponent