package com.plusmobileapps.sample.anvilkmp

import android.app.Application

class MyApplication : Application() {
    lateinit var component: AndroidAppComponent

    override fun onCreate() {
        super.onCreate()
        component = AndroidAppComponent.create(this)
    }
}