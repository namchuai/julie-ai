package ai.julie.feature.chat.navigation

import ai.julie.core.model.aimodel.AiModel
import ai.julie.core.model.aimodel.aiModelSerializersModule
import ai.julie.feature.chat.ChatScreenRoute
import ai.julie.feature.chat.ChatViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel

private val navigationJson = Json {
    serializersModule = aiModelSerializersModule
    ignoreUnknownKeys = true
}

@Serializable // New object for the chat graph's route
internal object ChatFlow 

@Serializable
internal class ChatScreen(val modelJson: String)

fun NavGraphBuilder.chatGraph(
    onModelMarketClick: () -> Unit,
    onModelManagementClick: () -> Unit,
) {
    navigation<ChatFlow>( // Use ChatFlow as the route for this nested graph
        startDestination = ChatScreen::class, // ChatScreen is the start destination *within* this graph
    ) {
        composable<ChatScreen> { entry -> // The route for this composable will be based on ChatScreen
            val viewModel = koinViewModel<ChatViewModel>()
            val modelJsonString = entry.toRoute<ChatScreen>().modelJson
            val aiModel = navigationJson.decodeFromString<AiModel>(modelJsonString)

            ChatScreenRoute(
                viewModel = viewModel,
                selectedModel = aiModel,
                onModelMarketClick = onModelMarketClick,
                onModelManagementClick = onModelManagementClick,
            )
        }
    }
}

fun NavController.navigateToChat(model: AiModel) {
    val modelJsonString = navigationJson.encodeToString(model)
    navigate(route = ChatScreen(modelJson = modelJsonString)) // Still navigate directly to ChatScreen
}
