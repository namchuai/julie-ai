package ai.julie.feature.chat

import ai.julie.core.designsystem.component.components.Button
import ai.julie.core.designsystem.component.components.Text
import ai.julie.resources.Res
import ai.julie.resources.new_chat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatScreenRoute(
    viewModel: ChatViewModel,
) {
    ChatScreen(
        viewModel::onSendClick,
        viewModel::onTestStorageClick,
    )
}

@Composable
fun ChatScreen(
    onSendClick: () -> Unit,
    onTestDatabase: () -> Unit,
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

        Button(
            onClick = onTestDatabase
        ) {
            Text("Test database")
        }
    }
}