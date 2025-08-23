package ai.julie.feature.modelmanagement.navigation

import ai.julie.feature.modelmanagement.ui.localmodelmanagement.LocalModelManagementScreenRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ai.julie.feature.modelmanagement.navigation.ModelManagementNavigation as ModelManagementNavigationInterface

/**
 * Implementation of ModelManagementNavigation interface.
 */
class ModelManagementNavigationImpl : ModelManagementNavigationInterface {

    override fun NavGraphBuilder.modelManagementGraph(
        onModelClick: () -> Unit,
        onNavigateToChat: () -> Unit,
        onBackClick: () -> Unit,
    ) {
        composable("modelManagement") {
            LocalModelManagementScreenRoute(
                onNavigateToChat = onNavigateToChat,
                onBackClick = onBackClick,
            )
        }
    }
}