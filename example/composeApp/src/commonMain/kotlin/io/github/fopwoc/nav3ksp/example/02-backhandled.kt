package io.github.fopwoc.nav3ksp.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.fopwoc.nav3ksp.annotation.Branch
import io.github.fopwoc.nav3ksp.annotation.Tree

@Tree
annotation class ExampleBackHandledTree

@OptIn(ExperimentalMaterial3Api::class)
@Branch(ExampleBackHandledTree::class)
@Composable
fun BackHandledView() {
    val backStack: NavBackStack<NavKey> = LocalBackStack.current

    NavigationBackHandler(
        isBackEnabled = true,
        state = rememberNavigationEventState(
            currentInfo = NavigationEventInfo.None,
        )
    ) {}

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Back Handled View") },
                navigationIcon = {
                    IconButton(
                        onClick = { backStack.removeLast() },
                        content = { Text("<") }
                    )
                }
            )
        }
    ) {
        Box(
            modifier = Modifier.padding(it).fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column {
                Text("Hi!")
                Text("You can't go back with swipe gesture")
                Button(
                    onClick = { backStack.removeLast() }
                ) {
                    Text("go back")
                }
            }
        }
    }
}
