package com.plusmobileapps.sample.anvilkmp

import com.plusmobileapps.sample.anvilkmp.di.AppComponent
import com.plusmobileapps.sample.anvilkmp.di.Singleton
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate

@Component
@Singleton
abstract class IosAppComponent : AppComponent {

    companion object
}

@KmpComponentCreate
expect fun IosAppComponent.Companion.createComponent(): IosAppComponent