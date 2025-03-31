package com.plusmobileapps.sample.anvilkmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.defaultComponentContext
import com.plusmobileapps.sample.anvilkmp.ui.RootScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val component = (application as MyApplication).component
        val rootBloc = component.rootBlocFactory.create(defaultComponentContext())
        setContent {
            RootScreen(bloc = rootBloc)
        }
    }
}