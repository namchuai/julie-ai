package ai.julie.navigation

import ai.julie.feature.chat.navigation.Chat
import ai.julie.feature.chat.navigation.chatGraph
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
    startDestination: Any = Chat,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        startDestination = startDestination,
        navController = navController,
    ) {
        chatGraph(
            navController = navController,
            onModelMarketClick = { navController.navigateToModelMarket() },
            onModelManagementClick = { navController.navigateToModelManagement() },
        )
        modelMarketGraph(
            onBackClick = navController::navigateUp,
        )
        modelManagementGraph(
            onBackClick = navController::navigateUp,
        )
    }
}