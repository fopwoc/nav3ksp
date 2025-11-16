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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.github.fopwoc.nav3ksp.annotation.Branch
import io.github.fopwoc.nav3ksp.annotation.Tree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Tree
annotation class ExampleViewModelTree

class ExampleViewModel : ViewModel() {
    val state = MutableStateFlow(0)

    fun inc() {
        state.update {
            it + 1
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Branch(ExampleViewModelTree::class)
@Composable
fun ExampleVmView(
    viewModel: ExampleViewModel = viewModel { ExampleViewModel() }
) {
    val backStack: NavBackStack<NavKey> = LocalBackStack.current
    val state by viewModel.state.asStateFlow().collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("View Model") },
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
