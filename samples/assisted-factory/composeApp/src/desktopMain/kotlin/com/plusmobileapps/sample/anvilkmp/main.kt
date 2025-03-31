package com.plusmobileapps.sample.anvilkmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Kotlin Inject Anvil Sample",
    ) {
        App()
    }
}