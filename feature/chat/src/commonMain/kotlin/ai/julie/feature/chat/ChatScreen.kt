package ai.julie.feature.chat

import ai.julie.core.designsystem.component.components.Accordion
import ai.julie.core.designsystem.component.components.Button
import ai.julie.core.designsystem.component.components.Scaffold
import ai.julie.core.designsystem.component.components.Text
import ai.julie.feature.modelconfig.screen.modelconfig.ModelConfigContent
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.thread.Thread

// TODO: NamH move the materia3 drawer to designsystem

@Composable
fun ChatScreenRoute(
    viewModel: ChatViewModel,
    onModelMarketClick: () -> Unit,
    onModelManagementClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    ChatScreen(
        threads = state.threads,
        message = state.message,
        messages = state.messages,
        onSendClick = viewModel::onSendClick,
        onNewChatClick = viewModel::onNewChatClick,
        onMessageUpdate = viewModel::onMessageUpdate,
        onModelMarketClick = onModelMarketClick,
        onModelManagementClick = onModelManagementClick,
    )
}

@Composable
fun ChatScreen(
    threads: List<Thread>,
    message: String,
    messages: List<MessageItem>,
    onSendClick: () -> Unit,
    onNewChatClick: () -> Unit,
    onMessageUpdate: (String) -> Unit,
    onModelMarketClick: () -> Unit = {},
    onModelManagementClick: () -> Unit = {},
) {
    ModalNavigationDrawer(
        gesturesEnabled = true,
        drawerContent = {

            ModalDrawerSheet {
                ModelConfigContent()

                Accordion(
                    headerContent = {
                        Text(
                            text = "Threads",
                            modifier = Modifier.padding(16.dp),
                        )
                    },
                    bodyContent = {
                        LazyColumn {
                            items(threads) {
                                Text(
                                    text = it.id.id,
                                    modifier = Modifier.padding(16.dp),
                                )
                            }
                        }
                    },
                )

                Button(
                    onClick = onModelManagementClick,
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text("Model Management")
                }
                Button(
                    onClick = onModelMarketClick,
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text("Model market")
                }
            }
        }
    ) {
        Scaffold(
//            topBar = {
//                TopBar {
//                    Text("Chat")
//                }
//            },
            content = {
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
                        onSendMessage = onSendClick,
                        modifier = Modifier.imePadding()
                    )
                }
            }
        )
    }
}

@Preview
@Composable
private fun ChatScreen_Preview() {
    ChatScreen(
        threads = emptyList(),
        message = "",
        messages = listOf(
            MessageItem(
                id = "1",
                content = "Hello",
                isFromUser = true,
            ),
            MessageItem(
                id = "2",
                content = "Hi",
                isFromUser = false,
            ),
        ),
        onSendClick = {},
        onNewChatClick = {},
        onMessageUpdate = {},
    )
}