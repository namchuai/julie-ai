package ai.julie.navigation

import ai.julie.feature.chat.navigation.Chat
import ai.julie.feature.chat.navigation.chatGraph
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
        )
    }
}