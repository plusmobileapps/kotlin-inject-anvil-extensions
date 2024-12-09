package com.plusmobileapps.sample.anvilkmp

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.ApplicationLifecycle
import com.plusmobileapps.sample.anvilkmp.ui.RootScreen

fun MainViewController() = ComposeUIViewController {
    val component = IosAppComponent.createComponent()
    val defaultContext = DefaultComponentContext(ApplicationLifecycle())
    RootScreen(bloc = component.rootBlocFactory.invoke(defaultContext))
}