package ai.julie.feature.chat

import ai.julie.core.designsystem.component.components.Button
import ai.julie.core.designsystem.component.components.Scaffold
import ai.julie.core.designsystem.component.components.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun ChatScreenRoute(
    viewModel: ChatViewModel,
    onModelMarketClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    ChatScreen(
        message = state.message,
        messages = state.messages,
        onSendClick = viewModel::onSendClick,
        onNewChatClick = viewModel::onNewChatClick,
        onMessageUpdate = viewModel::onMessageUpdate,
        onModelMarketClick = onModelMarketClick,
    )
}

@Composable
fun ChatScreen(
    message: String,
    messages: List<MessageItem>,
    onSendClick: () -> Unit,
    onNewChatClick: () -> Unit,
    onMessageUpdate: (String) -> Unit,
    onModelMarketClick: () -> Unit
) {
    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Button(onClick = onModelMarketClick) {
                    Text("Model market")
                }

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
                    onSendMessage = onSendClick,
                    modifier = Modifier.imePadding()
                )
            }
        }
    )
}
