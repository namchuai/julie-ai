package ai.julie

import ai.julie.component.windowcontrols.DraggableTitleBar
import ai.julie.component.windowcontrols.MacOSWindowControls
import ai.julie.core.designsystem.component.AppTheme
import ai.julie.core.designsystem.component.components.AlertDialog
import ai.julie.core.eventbus.ErrorEvent
import ai.julie.core.eventbus.EventBus
import ai.julie.navigation.DesktopNavGraph
import ai.julie.ui.bottomstatusbar.BottomStatusBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import java.awt.Window

@Composable
@Preview
fun DesktopApp(
    window: Window? = null
) {
    AppTheme {
        KoinContext {
            val eventBus = koinInject<EventBus>()
            var showErrorDialog by remember { mutableStateOf(false) }
            var errorTitle by remember { mutableStateOf("") }
            var errorMessage by remember { mutableStateOf("") }
            
            LaunchedEffect(eventBus) {
                eventBus.events().collect { event ->
                    when (event) {
                        is ErrorEvent -> {
                            errorTitle = event.title
                            errorMessage = event.message
                            showErrorDialog = true
                        }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                // Shadow layer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = 2.dp, x = 0.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .blur(radius = 4.dp)
                )

                // Main window content with gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF4A154B), // Deep purple (similar to Slack)
                                    Color(0xFF350D36)  // Darker purple
                                )
                            )
                        )
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        DraggableTitleBar(window = window) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MacOSWindowControls(
                                    window = window,
                                    onClose = { window?.dispose(); kotlin.system.exitProcess(0) }
                                )
                            }
                        }

                        // Main app content - use weight to fill remaining space
                        Box(modifier = Modifier.weight(1f)) {
                            DesktopNavGraph()
                        }

                        BottomStatusBar()
                    }
                }
            }
            
            // Global error dialog
            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showErrorDialog = false
                        errorMessage = ""
                        errorTitle = ""
                    },
                    onConfirmClick = {
                        showErrorDialog = false
                        errorMessage = ""
                        errorTitle = ""
                    },
                    title = errorTitle,
                    text = errorMessage,
                    confirmButtonText = "OK",
                    dismissButtonText = null
                )
            }
        }
    }
}