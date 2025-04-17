package ai.julie.feature.chat

import ai.julie.core.designsystem.component.components.Button
import ai.julie.core.designsystem.component.components.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import julie.feature.chat.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

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
    Text(
        text = stringResource(Res.string.new_chat)
    )
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