package ai.julie.feature.modelmanagement.navigation

import ai.julie.feature.modelmanagement.screen.localmodelmanagement.LocalModelManagementScreenRoute
import ai.julie.feature.modelmanagement.screen.localmodelmanagement.LocalModelManagementViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
internal data object ModelManagementScreen

fun NavController.navigateToModelManagement() = navigate(route = ModelManagementScreen)

fun NavGraphBuilder.modelManagementGraph(
    onBackClick: () -> Unit,
) {
    composable<ModelManagementScreen> {
        val viewModel = koinViewModel<LocalModelManagementViewModel>()
        LocalModelManagementScreenRoute(viewModel, onBackClick)
    }
}
