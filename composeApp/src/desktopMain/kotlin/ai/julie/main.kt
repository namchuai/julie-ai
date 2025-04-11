package ai.julie

import ai.julie.di.appModule
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(appModule)
    }
    return application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Julie",
        ) {
            App()
        }
    }
}