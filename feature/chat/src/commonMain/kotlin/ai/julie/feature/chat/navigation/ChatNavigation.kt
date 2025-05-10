package ai.julie.feature.chat.navigation

import ai.julie.feature.chat.ChatScreenRoute
import ai.julie.feature.chat.ChatViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object Chat

@Serializable
internal data object ChatScreen

fun NavGraphBuilder.chatGraph(
    navController: NavHostController,
    onModelMarketClick: () -> Unit,
    onModelManagementClick: () -> Unit,
) {
    navigation<Chat>(
        startDestination = ChatScreen,
    ) {
        composable<ChatScreen> {
            val viewModel = koinViewModel<ChatViewModel>()
            ChatScreenRoute(
                viewModel,
                onModelMarketClick,
                onModelManagementClick,
            )
        }
    }
}