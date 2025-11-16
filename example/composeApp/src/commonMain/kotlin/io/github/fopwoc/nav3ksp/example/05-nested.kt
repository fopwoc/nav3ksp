package io.github.fopwoc.nav3ksp.example

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.fopwoc.nav3ksp.NavDisplay
import io.github.fopwoc.nav3ksp.NavTreeBuilder
import io.github.fopwoc.nav3ksp.NavTreeLayout
import io.github.fopwoc.nav3ksp.annotation.Branch
import io.github.fopwoc.nav3ksp.annotation.Tree
import io.github.fopwoc.nav3ksp.example.nestedFirst.NestedFirstNavTree
import io.github.fopwoc.nav3ksp.example.nestedFirst.NestedFirstNavTreeBuilder
import io.github.fopwoc.nav3ksp.example.nestedFirst.NestedFirstNavTreeLayout
import io.github.fopwoc.nav3ksp.example.nestedSecond.NestedSecondNavTree
import io.github.fopwoc.nav3ksp.example.nestedSecond.NestedSecondNavTreeBuilder
import io.github.fopwoc.nav3ksp.example.nestedSecond.NestedSecondNavTreeLayout
import io.github.fopwoc.nav3ksp.example.nestedThird.NestedThirdNavTree
import io.github.fopwoc.nav3ksp.example.nestedThird.NestedThirdNavTreeBuilder
import io.github.fopwoc.nav3ksp.example.nestedThird.NestedThirdNavTreeLayout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// First View

@Tree
annotation class NestedFirst

class FirstViewModel : ViewModel() {
    val state = MutableStateFlow(0)

    fun inc() {
        state.update {
            it + 1
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Branch(NestedFirst::class)
@Composable
fun FirstView(
    viewModel: FirstViewModel = viewModel { FirstViewModel() }
) {
    val state by viewModel.state.asStateFlow().collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("First Nested View") },
            )
        }
    ) {
        Box(
            modifier = Modifier.padding(it).fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column {
                Text("Hi! $state")
                Button(
                    onClick = viewModel::inc
                ) {
                    Text("Inc")
                }
            }
        }
    }
}

// Second View

@Tree
annotation class NestedSecond

class SecondViewModel : ViewModel() {
    val state = MutableStateFlow(0)

    fun inc() {
        state.update {
            it + 1
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Branch(NestedSecond::class)
@Composable
fun SecondView(
    viewModel: FirstViewModel = viewModel { FirstViewModel() }
) {
    val backStack: NavBackStack<NavKey> = LocalBackStack.current

    val state by viewModel.state.asStateFlow().collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Second Nested View") },
            )
        }
    ) {
        Box(
            modifier = Modifier.padding(it).fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column {
                Button(
                    onClick = viewModel::inc
                ) {
                    Text("Inc")
                }

                Button(
                    onClick = {
                        backStack.add(NestedSecondNavTree.SecondItem(state))
                    }
                ) {
                    Text("Show counter value")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Branch(NestedSecond::class)
@Composable
fun SecondItemView(
    value: Int,
) {
    val backStack: NavBackStack<NavKey> = LocalBackStack.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item") },
                navigationIcon = {
                    IconButton(
                        onClick = { backStack.removeLast() },
                        content = { Text("<") }
                    )
                }
            )
        },
    ) {
        Box(
            modifier = Modifier.padding(it).fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column {
                Text("counter value is $value")
            }
        }
    }
}

// Third View

@Tree
annotation class NestedThird

@OptIn(ExperimentalMaterial3Api::class)
@Branch(NestedThird::class)
@Composable
fun ThirdView() {
    val rootBackStack: NavBackStack<NavKey> = LocalRootBackStack.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Third Nested View") },
            )
        },
    ) {
        Box(
            modifier = Modifier.padding(it).fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Button(
                onClick = {
                    rootBackStack.removeLast()
                }
            ) {
                Text("Go back to main menu")
            }
        }
    }
}

// Nested View

val LocalRootBackStack = compositionLocalOf<NavBackStack<NavKey>> {
    error("No LocalRootBackStack provided")
}

@Tree(
    subTree = [
        NestedFirst::class,
        NestedSecond::class,
        NestedThird::class,
    ]
)
annotation class ExampleNestedTree

enum class Items(
    val starKey: NavKey,
    val treeLayout: NavTreeLayout,
    val treeBuilder: NavTreeBuilder,
) {
    First(
        starKey = NestedFirstNavTree.First,
        treeLayout = NestedFirstNavTreeLayout,
        treeBuilder = NestedFirstNavTreeBuilder
    ),
    Second(
        starKey = NestedSecondNavTree.Second,
        treeLayout = NestedSecondNavTreeLayout,
        treeBuilder = NestedSecondNavTreeBuilder
    ),
    Third(
        starKey = NestedThirdNavTree.Third,
        treeLayout = NestedThirdNavTreeLayout,
        treeBuilder = NestedThirdNavTreeBuilder
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Branch(ExampleNestedTree::class)
@Composable
fun NestedView() {
    val backStack = LocalBackStack.current
    var currentPage by remember { mutableStateOf(Items.First) }
    val pages = remember { Items.entries }

    NavigationBackHandler(
        isBackEnabled = true,
        state = rememberNavigationEventState(
            currentInfo = NavigationEventInfo.None,
        )
    ) {}

    CompositionLocalProvider(
        LocalRootBackStack provides backStack
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    pages.forEach { page ->
                        NavigationBarItem(
                            selected = page == currentPage,
                            onClick = {
                                currentPage = page
                            },
                            icon = { Text(page.name) },
                        )
                    }
                }
            }
        ) {
            Crossfade(currentPage) { currentPage ->
                CompositionLocalProvider(
                    LocalBackStack provides currentPage.treeLayout.rememberTreeBackStack(currentPage.starKey)
                ) {
                    NavDisplay(
                        navTreeBuilder = currentPage.treeBuilder,
                        backStackLocalComposition = LocalBackStack
                    )
                }
            }
        }
    }
}
