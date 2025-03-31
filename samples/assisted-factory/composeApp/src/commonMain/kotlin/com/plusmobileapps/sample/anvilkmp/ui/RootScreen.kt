package com.plusmobileapps.sample.anvilkmp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.plusmobileapps.sample.anvilkmp.blocs.RootBloc

@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    bloc: RootBloc
) {
    val routerState = bloc.routerState.subscribeAsState()
    MaterialTheme {
        Children(
            modifier = modifier.fillMaxSize(),
            stack = routerState.value,
        ) { child ->
            when (val instance = child.instance) {
                is RootBloc.Child.Home -> HomeScreen(bloc = instance.bloc)
            }
        }
    }
}