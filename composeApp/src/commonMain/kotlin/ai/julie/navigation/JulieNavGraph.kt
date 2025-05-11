package ai.julie.navigation

import ai.julie.feature.chat.navigation.chatGraph
import ai.julie.feature.chat.navigation.navigateToChat
import ai.julie.feature.modelmanagement.navigation.ModelManagement
import ai.julie.feature.modelmanagement.navigation.modelManagementGraph
import ai.julie.feature.modelmanagement.navigation.navigateToModelManagement
import ai.julie.feature.modelmarket.navigation.modelMarketGraph
import ai.julie.feature.modelmarket.navigation.navigateToModelMarket
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun JulieNavGraph(
    modifier: Modifier = Modifier,
    startDestination: Any = ModelManagement,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        startDestination = startDestination,
        navController = navController,
    ) {
        modelManagementGraph(
            onModelClick = { navController.navigateToChat(it) },
            onBackClick = navController::navigateUp,
        )
        chatGraph(
            onModelMarketClick = { navController.navigateToModelMarket() },
            onModelManagementClick = { navController.navigateToModelManagement() },
        )
        modelMarketGraph(
            onBackClick = navController::navigateUp,
        )
    }
}