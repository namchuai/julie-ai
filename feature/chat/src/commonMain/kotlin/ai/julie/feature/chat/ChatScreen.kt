package ai.julie.feature.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ChatScreenRoute(
    viewModel: ChatViewModel,
) {
    ChatScreen()
}

@Composable
fun ChatScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text("Chat screen")
    }
}