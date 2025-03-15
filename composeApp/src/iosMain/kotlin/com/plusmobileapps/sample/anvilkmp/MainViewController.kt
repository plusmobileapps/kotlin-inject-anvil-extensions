package com.plusmobileapps.sample.anvilkmp

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.ApplicationLifecycle
import com.plusmobileapps.sample.anvilkmp.ui.RootScreen
import platform.UIKit.UIApplication

fun MainViewController() = ComposeUIViewController {
    val component = IosAppComponent::class.createIosAppComponent(UIApplication.sharedApplication)
    val defaultContext = DefaultComponentContext(ApplicationLifecycle())
    RootScreen(bloc = component.rootBlocFactory.invoke(defaultContext))
}