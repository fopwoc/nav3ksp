package io.github.fopwoc.nav3ksp.example

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Nav3Ksp") {
        App()
    }
}
