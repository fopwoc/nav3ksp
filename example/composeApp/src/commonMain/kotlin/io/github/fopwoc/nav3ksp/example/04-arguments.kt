package io.github.fopwoc.nav3ksp.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.github.fopwoc.nav3ksp.annotation.Branch
import io.github.fopwoc.nav3ksp.annotation.Tree
import io.github.fopwoc.nav3ksp.example.exampleArgumentsTree.ExampleArgumentsNavTree
import kotlin.random.Random

@Tree
annotation class ExampleArgumentsTree

@OptIn(ExperimentalMaterial3Api::class)
@Branch(ExampleArgumentsTree::class)
@Composable
fun ListView() {
    val backStack: NavBackStack<NavKey> = LocalBackStack.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List") },
                navigationIcon = {
                    IconButton(
                        onClick = { backStack.removeLast() },
                        content = { Text("<") }
                    )
                }
            )
        }
    ) {
        val list = remember { (0..100).toList() }

        LazyColumn(
            modifier = Modifier.padding(it).fillMaxSize()
        ) {
            items(list) { item ->
                val color = remember(item) { Color(Random.nextLong()).copy(alpha = 0.5f) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color)
                        .clickable {
                            backStack.add(ExampleArgumentsNavTree.Item(item))
                        }
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 16.dp),
                        text = "item №$item"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Branch(ExampleArgumentsTree::class)
@Composable
fun ItemView(
    value: Int,
) {
    val backStack: NavBackStack<NavKey> = LocalBackStack.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item №$value") },
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
            Text("You clicked on item №$value")
        }
    }
}
