package ai.julie.feature.chat.ui.chatinput

import ai.julie.core.designsystem.component.components.Icon
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.designsystem.component.components.textfield.TextField
import ai.julie.core.domain.session.PromptSessionState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatInputContainer() {
    val viewModel = koinViewModel<ChatInputViewModel>()
    val state by viewModel.state.collectAsState()

    ChatInputContent(
        state = state,
        onMessageUpdate = viewModel::onMessageUpdate,
        onSendClick = viewModel::onSendClick,
        onCancelClick = viewModel::onCancelClick,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatInputContent(
    state: ChatInputState,
    onMessageUpdate: (String) -> Unit,
    onSendClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    val isSessionRunning = state.sessionState is PromptSessionState.Running
    Row(
        modifier = Modifier
            .imePadding()
            .fillMaxWidth()
            .onPreviewKeyEvent { keyEvent ->
                when {
                    keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyDown -> {
                        if (!keyEvent.isShiftPressed && state.message.isNotBlank()) {
                            // Enter without Shift: Send message
                            onSendClick()
                            true // Consume the event
                        } else {
                            // Shift+Enter or empty message: Let it pass through
                            false
                        }
                    }
                    else -> false
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = state.message,
            onValueChange = { newValue ->
                // Check if Enter was added (but not with Shift)
                val oldLines = state.message.count { it == '\n' }
                val newLines = newValue.count { it == '\n' }
                
                if (newLines > oldLines && !newValue.endsWith("\n")) {
                    // This means a newline was added in the middle, let it through
                    onMessageUpdate(newValue)
                } else if (newValue.endsWith("\n") && newLines == oldLines + 1) {
                    // New line at the end - this is likely from Enter key
                    // Don't add the newline, just send if not empty
                    if (state.message.isNotBlank()) {
                        onSendClick()
                    }
                } else {
                    // Normal text change
                    onMessageUpdate(newValue)
                }
            },
            placeholder = { Text("Message..") },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (state.message.isNotBlank()) {
                        onSendClick()
                    }
                }
            ),
            singleLine = false,
            maxLines = 5
        )

        // Send/Stop button
        FloatingActionButton(
            onClick = if (isSessionRunning) onCancelClick else onSendClick,
            containerColor = if (isSessionRunning) Color(0xFFFFCDD2) else Color(0xFFBBDEFB),
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                if (isSessionRunning) Icons.Default.Stop else Icons.Default.ArrowUpward,
                contentDescription = if (isSessionRunning) "Stop" else "Send",
                tint = if (isSessionRunning) Color.Red else Color.Blue
            )
        }
    }
}
