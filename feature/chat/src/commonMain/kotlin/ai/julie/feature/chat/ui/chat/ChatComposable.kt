package ai.julie.feature.chat.ui.chat

import ai.julie.core.common.AsyncState
import ai.julie.core.designsystem.component.components.Text
import ai.julie.feature.chat.ChatMessageList
import ai.julie.feature.chat.ui.chatinput.ChatInputContainer
import ai.julie.feature.thread.ui.threadlisting.noOp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatComposable() {
    val viewModel = koinViewModel<ChatViewModel>()
    val state by viewModel.state.collectAsState()

    ChatContent(state = state)
}

@Composable
internal fun ChatContent(
    state: AsyncState<ChatState>,
) {
    when (state) {
        is AsyncState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Loading chat...")
            }
        }

        is AsyncState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier.weight(1.0f)
                ) {
                    ChatMessageList(messages = state.value.messages)
                }

                ChatInputContainer()
            }
        }

        else -> noOp()
    }
}
