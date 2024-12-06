package com.plusmobileapps.sample.anvilkmp

import android.app.Application
import com.plusmobileapps.sample.anvilkmp.di.AppComponent
import com.plusmobileapps.sample.anvilkmp.di.create

class MyApplication : Application() {
    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        component = AppComponent::class.create()
    }
}