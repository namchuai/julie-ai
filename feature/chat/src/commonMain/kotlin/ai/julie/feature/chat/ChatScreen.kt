package ai.julie.feature.chat

import ai.julie.core.designsystem.component.components.Button
import ai.julie.core.designsystem.component.components.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChatScreenRoute(
    viewModel: ChatViewModel,
    onModelMarketClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    ChatScreen(
        viewModel = viewModel,
        message = state.message,
        messages = state.messages,
        onSendClick = viewModel::onSendClick,
        onNewChatClick = viewModel::onNewChatClick,
        onMessageUpdate = viewModel::onMessageUpdate,
        onTriggerLlamaLoad = viewModel::onTestLlamaLoadClick,
        onModelMarketClick = onModelMarketClick,
    )
}

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    message: String,
    messages: List<MessageItem>,
    onSendClick: () -> Unit,
    onNewChatClick: () -> Unit,
    onMessageUpdate: (String) -> Unit,
    onTriggerLlamaLoad: () -> Unit,
    onModelMarketClick: () -> Unit
) {
    // val testLog by viewModel.testLog.collectAsState() // Removed

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Button(onClick = onModelMarketClick) {
            Text("Model market")
        }

        // --- Test Section ---
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Button(onClick = { onTriggerLlamaLoad() }) {
                Text("Test Load Llama Model (Desktop Only)")
            }
        }
        // Removed Text area for log display
        Spacer(modifier = Modifier.height(8.dp))
        // --- End Test Section ---

        ChatMessageList(
            messages = messages,
            modifier = Modifier.weight(1f)
        )

        NewChatButton(
            onClick = onNewChatClick,
        )

        ChatInputSection(
            message = message,
            onMessageUpdate = onMessageUpdate,
            onSendMessage = onSendClick
        )
    }
}
