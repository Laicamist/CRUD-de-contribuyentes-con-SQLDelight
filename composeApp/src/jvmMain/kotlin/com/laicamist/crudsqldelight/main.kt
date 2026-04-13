package com.laicamist.crudsqldelight

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.laicamist.crudsqldelight.cache.JvmDatabaseDriverFactory

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "crudsqldelight",
    ) {
        App(JvmDatabaseDriverFactory())
    }
}