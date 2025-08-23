package ai.julie

import ai.julie.di.desktopAppModule
import ai.julie.feature.appsetting.data.MainWindowRepository
import ai.julie.feature.appsetting.domain.UpdateMainWindowSetting
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import io.github.vinceglb.filekit.FileKit
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.koin.core.context.startKoin

fun main() {
    // Initialize FileKit
    FileKit.init(appId = "Julie")

    val koin = startKoin {
        modules(desktopAppModule)
    }.koin

    // Load window settings before creating the window
    val mainWindowRepository = koin.get<MainWindowRepository>()
    val initialSettings = runBlocking {
        mainWindowRepository.getMainWindowSetting()
    }
    
    return application {
        val windowSetting = initialSettings
        
        val windowState = rememberWindowState(
            position = WindowPosition(windowSetting.x.dp, windowSetting.y.dp),
            size = DpSize(windowSetting.width.dp, windowSetting.height.dp)
        )
        
        // Track when window settings should be saved with debounce
        var pendingSave by remember { mutableStateOf(false) }
        
        Window(
            onCloseRequest = ::exitApplication,
            title = "Julie",
            state = windowState,
            undecorated = true,
            transparent = true,
        ) {
            // Trigger save when window position or size changes
            LaunchedEffect(windowState.position, windowState.size) {
                pendingSave = true
            }
            
            // Debounced save effect
            LaunchedEffect(pendingSave) {
                if (pendingSave) {
                    delay(500) // 0.5 second debounce
                    if (pendingSave) { // Check again after delay to avoid race conditions
                        val updateMainWindowSetting = koin.get<UpdateMainWindowSetting>()
                        val currentSettings = mainWindowRepository.getMainWindowSetting()
                        updateMainWindowSetting.updateMainWindowSetting(
                            currentSettings.copy(
                                x = windowState.position.x.value.toInt(),
                                y = windowState.position.y.value.toInt(),
                                width = windowState.size.width.value.toInt(),
                                height = windowState.size.height.value.toInt()
                            )
                        )
                        pendingSave = false
                    }
                }
            }
            
            DevelopmentEntryPoint {
                DesktopApp(
                    window = window,
                    windowState = windowState,
                )
            }
        }
    }
}