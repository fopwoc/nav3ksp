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
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import io.github.fopwoc.nav3ksp.annotation.Branch
import io.github.fopwoc.nav3ksp.annotation.Tree
import io.github.fopwoc.nav3ksp.example.exampleResultTree.ExampleResultNavTree
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

val LocalResultEventBus = compositionLocalOf<ResultEventBus> {
    ResultEventBus()
}

data class FormSubmitEvent(
    val text: String
)

class ResultViewModel : ViewModel() {
    val state = MutableStateFlow("")

    fun onResultEvent(value: FormSubmitEvent) {
        state.update {
            value.text
        }
    }
}

@Tree
annotation class ExampleResultTree

@OptIn(ExperimentalMaterial3Api::class)
@Branch(ExampleResultTree::class)
@Composable
fun ResultView(
    viewMode: ResultViewModel = viewModel { ResultViewModel() }
) {
    val backStack: NavBackStack<NavKey> = LocalBackStack.current

    val state by viewMode.state.asStateFlow().collectAsState()

    ResultEffect<FormSubmitEvent>(onResult = viewMode::onResultEvent)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Result View") },
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
                Text("Current value is \"$state\"")
                Button(
                    onClick = {
                        backStack.add(ExampleResultNavTree.Form)
                    }
                ) {
                    Text("Go to form")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Branch(ExampleResultTree::class)
@Composable
fun FormView() {
    val backStack: NavBackStack<NavKey> = LocalBackStack.current
    val eventBus: ResultEventBus = LocalResultEventBus.current

    var value by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Form") },
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
                TextField(
                    value = value,
                    onValueChange = {
                        value = it
                    }
                )

                Button(
                    onClick = {
                        eventBus.sendResult(result = FormSubmitEvent(value))
                        backStack.removeLast()
                    }
                ) {
                    Text("Submit")
                }
            }
        }
    }
}

// this awesome piece of code below taken from https://github.com/android/nav3-recipes/blob/main/app/src/main/java/com/example/nav3recipes/results/event/ResultEventBus.kt

class ResultEventBus {
    val channelMap: MutableMap<String, Channel<Any?>> = mutableMapOf()

    inline fun <reified T> getResultFlow(resultKey: String = T::class.toString()) =
        channelMap[resultKey]?.receiveAsFlow()

    inline fun <reified T> sendResult(resultKey: String = T::class.toString(), result: T) {
        if (!channelMap.contains(resultKey)) {
            channelMap[resultKey] = Channel(capacity = BUFFERED, onBufferOverflow = BufferOverflow.SUSPEND)
        }
        channelMap[resultKey]?.trySend(result)
    }

    inline fun <reified T> removeResult(resultKey: String = T::class.toString()) {
        channelMap.remove(resultKey)
    }
}

@Composable
inline fun <reified T> ResultEffect(
    resultEventBus: ResultEventBus = LocalResultEventBus.current,
    resultKey: String = T::class.toString(),
    crossinline onResult: suspend (T) -> Unit
) {
    LaunchedEffect(resultKey, resultEventBus.channelMap[resultKey]) {
        resultEventBus.getResultFlow<T>(resultKey)?.collect { result ->
            onResult.invoke(result as T)
        }
    }
}
