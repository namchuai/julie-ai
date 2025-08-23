package ai.julie.panel.chat

import ai.julie.core.common.AsyncState
import ai.julie.core.designsystem.component.AppTheme
import ai.julie.core.designsystem.component.components.Text
import ai.julie.feature.chat.ui.chat.ChatComposable
import ai.julie.feature.modelmanagement.ui.modelinfo.ModelInfoContainer
import ai.julie.feature.thread.ui.threadlisting.ThreadListingScreenRoute
import ai.julie.feature.thread.ui.threadlisting.noOp
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatPanel() {
    val density = LocalDensity.current
    var threadPanelWidth by remember { mutableStateOf(300.dp) }
    val minThreadWidth = 200.dp
    val maxThreadWidth = 600.dp

    var modelConfigWidth by remember { mutableStateOf(300.dp) }
    val minModelConfigWidth = 200.dp
    val maxModelConfigWidth = 600.dp

    val viewModel = koinViewModel<ChatPanelViewModel>()
    val state by viewModel.state.collectAsState()

    when (state) {
        is AsyncState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is AsyncState.Success -> {
            val successState = state as AsyncState.Success<ChatPanelState>
            Row(
                modifier = Modifier.fillMaxSize(),
            ) {
                if (successState.value.threads.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .width(threadPanelWidth)
                            .fillMaxHeight()
                    ) {
                        ThreadListingScreenRoute()
                    }

                    // Draggable divider
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .fillMaxHeight()
                            .background(AppTheme.colors.onSurface.copy(alpha = 0.1f))
                            .pointerHoverIcon(PointerIcon.Hand)
                            .pointerInput(Unit) {
                                detectDragGestures { _, dragAmount ->
                                    val deltaX = dragAmount.x
                                    val newWidth =
                                        threadPanelWidth + with(density) { deltaX.toDp() }
                                    threadPanelWidth =
                                        newWidth.coerceIn(minThreadWidth, maxThreadWidth)
                                }
                            }
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    if (successState.value.activeThread == null) {
                        ChatPlaceholder()
                    } else {
                        ChatComposable()
                    }
                }

                if (successState.value.activeThread != null) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .fillMaxHeight()
                            .background(AppTheme.colors.onSurface.copy(alpha = 0.1f))
                            .pointerHoverIcon(PointerIcon.Hand)
                            .pointerInput(Unit) {
                                detectDragGestures { _, dragAmount ->
                                    val deltaX = dragAmount.x
                                    val newWidth =
                                        modelConfigWidth - with(density) { deltaX.toDp() }
                                    modelConfigWidth =
                                        newWidth.coerceIn(minModelConfigWidth, maxModelConfigWidth)
                                }
                            }
                    )

                    Box(
                        modifier = Modifier
                            .width(modelConfigWidth)
                            .fillMaxHeight()
                    ) {
                        ModelInfoContainer()
                    }
                }
            }
        }

        else -> noOp()
    }
}

@Composable
internal fun ChatPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No Active Thread",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select a model and start a new chat to begin",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
