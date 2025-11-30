package io.github.fopwoc.nav3ksp.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import io.github.fopwoc.nav3ksp.NavTree
import io.github.fopwoc.nav3ksp.NavTreeBuilder
import io.github.fopwoc.nav3ksp.NavTreeLayout
import io.github.fopwoc.nav3ksp.annotation.Branch
import io.github.fopwoc.nav3ksp.annotation.Tree
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass

@Tree
annotation class ExampleManualTree

@OptIn(ExperimentalMaterial3Api::class)
@Branch(ExampleManualTree::class)
@Composable
fun ExampleManualView() {
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
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground))
            ) {
                androidx.navigation3.ui.NavDisplay( // not proxy!
                    backStack = ManualNavTree.treeLayout.rememberTreeBackStack(ManualNavTree.View),
                    entryProvider = entryProvider {
                        with(ManualNavTree.TreeBuilder) {
                            buildTree()
                        }
                    }
                )
            }
        }
    }
}

// Manual

object ManualNavTree : NavTree {
    @Serializable
    data object View : NavKey

    val TreeBuilder = object : NavTreeBuilder() {
        override fun EntryProviderScope<NavKey>.branchesEntry() {
            entry<View> {
                ManualView()
            }
        }
    }

    val treeLayout = object : NavTreeLayout() {
        override fun PolymorphicModuleBuilder<NavKey>.polymorphicSerializationSubClasses() {
            subclass(serializer = View.serializer())
        }
    }
}

@Composable
fun ManualView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("I'm another NavDisplay!")
            Text("boilerplated by hand")
        }
    }
}
