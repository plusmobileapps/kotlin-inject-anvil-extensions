package com.plusmobileapps.sample.anvilkmp

import com.plusmobileapps.sample.anvilkmp.di.AppComponent
import com.plusmobileapps.sample.anvilkmp.di.Singleton
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@Singleton
abstract class AndroidAppComponent(
    @get:Provides val application: MyApplication,
) : AppComponent {

    companion object
}