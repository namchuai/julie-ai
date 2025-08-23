package ai.julie.feature.chat.navigation

import ai.julie.feature.chat.ui.chat.ChatComposable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
internal class ChatScreen

fun NavGraphBuilder.chatGraph() {
    composable<ChatScreen> {
        ChatComposable()
    }
}

fun NavController.navigateToChat() {
    navigate(route = ChatScreen())
}
