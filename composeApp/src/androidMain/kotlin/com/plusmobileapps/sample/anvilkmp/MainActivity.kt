package com.plusmobileapps.sample.anvilkmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.defaultComponentContext
import com.plusmobileapps.sample.anvilkmp.ui.RootScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val component = (application as MyApplication).component
        val rootBloc = component.rootBlocFactory(defaultComponentContext())
        setContent {
            RootScreen(bloc = rootBloc)
        }
    }
}