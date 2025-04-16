package ai.julie.feature.chat

import ai.julie.core.designsystem.component.components.Button
import ai.julie.core.designsystem.component.components.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ChatScreenRoute(
    viewModel: ChatViewModel,
) {
    ChatScreen(viewModel::onSendClick)
}

@Composable
fun ChatScreen(
    onSendClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Button(
            onClick = onSendClick,
        ) {
            Text("Send")
        }
    }
}