package com.plusmobileapps.sample.anvilkmp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.plusmobileapps.sample.anvilkmp.blocs.HomeBloc

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    bloc: HomeBloc,
) {
    val models = bloc.models.subscribeAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Home") }) }
    ) {
        Box(
            modifier = Modifier.padding(it),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = models.value.greeting.greet())
        }
    }
}