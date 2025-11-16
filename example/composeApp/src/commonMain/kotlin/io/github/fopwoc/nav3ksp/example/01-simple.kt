package io.github.fopwoc.nav3ksp.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import io.github.fopwoc.nav3ksp.annotation.Branch
import io.github.fopwoc.nav3ksp.annotation.Tree

@Tree
annotation class ExampleSimpleTree

@OptIn(ExperimentalMaterial3Api::class)
@Branch(ExampleSimpleTree::class)
@Composable
fun SimpleView() {
    val backStack: NavBackStack<NavKey> = LocalBackStack.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simple View") },
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
            Text("Hello World!")
        }
    }
}
