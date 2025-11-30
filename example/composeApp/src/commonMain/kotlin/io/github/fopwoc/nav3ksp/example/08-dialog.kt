package io.github.fopwoc.nav3ksp.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import io.github.fopwoc.nav3ksp.annotation.Branch
import io.github.fopwoc.nav3ksp.annotation.BranchEntryMetadata
import io.github.fopwoc.nav3ksp.annotation.Tree
import io.github.fopwoc.nav3ksp.example.exampleDialogTree.ExampleDialogNavTree

/**
 * also add `sceneStrategy = remember { DialogSceneStrategy<NavKey>() }` to your [NavDisplay]
 */
data object DialogEntryMetadata : BranchEntryMetadata {
    override fun metadata() = DialogSceneStrategy.dialog(
        DialogProperties(
            usePlatformDefaultWidth = false
        )
    )
}

@Tree
annotation class ExampleDialogTree

@OptIn(ExperimentalMaterial3Api::class)
@Branch(ExampleDialogTree::class)
@Composable
fun DialogExampleView() {
    val backStack: NavBackStack<NavKey> = LocalBackStack.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dialog View") },
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
                Button(
                    onClick = {
                        backStack.add(ExampleDialogNavTree.Dialog)
                    }
                ) {
                    Text("Call dialog")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Branch(ExampleDialogTree::class, metadata = DialogEntryMetadata::class)
@Composable
fun DialogView() {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Hello world!")
        }
    }
}
