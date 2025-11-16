package io.github.fopwoc.nav3ksp.example

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.github.fopwoc.nav3ksp.NavDisplay
import io.github.fopwoc.nav3ksp.NavTree
import io.github.fopwoc.nav3ksp.NavTreeBuilder
import io.github.fopwoc.nav3ksp.NavTreeLayout
import io.github.fopwoc.nav3ksp.annotation.Branch
import io.github.fopwoc.nav3ksp.annotation.Tree
import io.github.fopwoc.nav3ksp.example.exampleArgumentsTree.ExampleArgumentsNavTree
import io.github.fopwoc.nav3ksp.example.exampleBackHandledTree.ExampleBackHandledNavTree
import io.github.fopwoc.nav3ksp.example.exampleManualTree.ExampleManualNavTree
import io.github.fopwoc.nav3ksp.example.exampleNestedTree.ExampleNestedNavTree
import io.github.fopwoc.nav3ksp.example.exampleResultTree.ExampleResultNavTree
import io.github.fopwoc.nav3ksp.example.exampleSimpleTree.ExampleSimpleNavTree
import io.github.fopwoc.nav3ksp.example.exampleViewModelTree.ExampleViewModelNavTree
import io.github.fopwoc.nav3ksp.example.nestedFirst.NestedFirstNavTreeLayout
import io.github.fopwoc.nav3ksp.example.nestedSecond.NestedSecondNavTreeLayout
import io.github.fopwoc.nav3ksp.example.nestedThird.NestedThirdNavTreeLayout
import io.github.fopwoc.nav3ksp.example.rootTree.RootNavTree
import io.github.fopwoc.nav3ksp.example.rootTree.RootNavTreeBuilder
import io.github.fopwoc.nav3ksp.example.rootTree.RootNavTreeLayout
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass

@Composable
fun App() {
    MaterialTheme {
        CompositionLocalProvider(
            LocalBackStack provides RootNavTreeLayout.rememberTreeBackStack(RootNavTree.Entrypoint)
        ) {
            NavDisplay(
                navTreeBuilder = RootNavTreeBuilder,
                backStackLocalComposition = LocalBackStack
            )
        }
    }
}

val LocalBackStack = compositionLocalOf<NavBackStack<NavKey>> {
    error("No LocalBackStack provided")
}

@Tree(
    subTree = [
        ExampleSimpleTree::class,
        ExampleBackHandledTree::class,
        ExampleViewModelTree::class,
        ExampleArgumentsTree::class,
        ExampleNestedTree::class,
        ExampleResultTree::class,
        ExampleManualTree::class,
    ]
)
annotation class RootTree

@OptIn(ExperimentalMaterial3Api::class)
@Branch(RootTree::class)
@Composable
fun EntrypointView() {
    val backStack: NavBackStack<NavKey> = LocalBackStack.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nav3Ksp Example") }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it).fillMaxSize()
        ) {
            item {
                Button(
                    onClick = {
                        backStack.add(ExampleSimpleNavTree.Simple)
                    }
                ) {
                    Text("Simple navigation example")
                }
            }

            item {
                Button(
                    onClick = {
                        backStack.add(ExampleBackHandledNavTree.BackHandled)
                    }
                ) {
                    Text("Backhandled view")
                }
            }

            item {
                Button(
                    onClick = {
                        backStack.add(ExampleViewModelNavTree.ExampleVm)
                    }
                ) {
                    Text("View model example")
                }
            }

            item {
                Button(
                    onClick = {
                        backStack.add(ExampleArgumentsNavTree.List)
                    }
                ) {
                    Text("Navigation with argument")
                }
            }

            item {
                Button(
                    onClick = {
                        backStack.add(ExampleNestedNavTree.Nested)
                    }
                ) {
                    Text("Nested View")
                }
            }

            item {
                Button(
                    onClick = {
                        backStack.add(ExampleResultNavTree.Result)
                    }
                ) {
                    Text("Navigation with result")
                }
            }

            item {
                Button(
                    onClick = {
                        backStack.add(ExampleManualNavTree.ExampleManual)
                    }
                ) {
                    Text("Manual NavDisplay")
                }
            }
        }
    }
}
