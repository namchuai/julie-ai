package ai.julie.navigation

import ai.julie.feature.chat.navigation.chatGraph
import ai.julie.feature.chat.navigation.navigateToChat
import ai.julie.feature.modelmanagement.navigation.ModelManagementNavigation
import ai.julie.feature.modelmanagement.navigation.modelManagementGraph
import ai.julie.feature.modelmarket.navigation.modelMarketGraph
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject

@Composable
fun JulieNavGraph(
    modifier: Modifier = Modifier,
    startDestination: String = "modelManagement",
    navController: NavHostController = rememberNavController(),
) {
    val modelManagementNavigation = koinInject<ModelManagementNavigation>()

    NavHost(
        modifier = modifier,
        startDestination = startDestination,
        navController = navController,
    ) {
        modelManagementGraph(
            navigation = modelManagementNavigation,
            onModelClick = { navController.navigateToChat() },
            onNavigateToChat = { navController.navigateToChat() },
            onBackClick = navController::navigateUp,
        )
        chatGraph()
        modelMarketGraph(
            onBackClick = navController::navigateUp,
        )
    }
}
