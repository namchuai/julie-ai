package ai.julie.feature.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ChatScreenRoute(
    viewModel: ChatViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ChatScreen(
        message = state.message,
        messages = state.messages,
        onSendClick = viewModel::onSendClick,
        onNewChatClick = viewModel::onNewChatClick,
        onMessageUpdate = viewModel::onMessageUpdate,
    )
}

@Composable
fun ChatScreen(
    message: String,
    messages: List<MessageItem>,
    onSendClick: () -> Unit,
    onNewChatClick: () -> Unit,
    onMessageUpdate: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
