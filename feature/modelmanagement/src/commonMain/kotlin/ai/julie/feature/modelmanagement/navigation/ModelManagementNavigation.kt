package ai.julie.feature.modelmanagement.navigation

import ai.julie.core.model.aimodel.AiModel
import ai.julie.feature.modelmanagement.screen.localmodelmanagement.LocalModelManagementScreenRoute
import ai.julie.feature.modelmanagement.screen.localmodelmanagement.LocalModelManagementViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object ModelManagement

@Serializable
data object ModelManagementScreen

fun NavController.navigateToModelManagement() = navigate(route = ModelManagementScreen)

fun NavGraphBuilder.modelManagementGraph(
    onModelClick: (AiModel) -> Unit = {},
    onBackClick: () -> Unit,
) {
    composable<ModelManagement> {
        val viewModel = koinViewModel<LocalModelManagementViewModel>()
        LocalModelManagementScreenRoute(
            viewModel = viewModel,
            onModelClick = onModelClick,
            onBackClick = onBackClick,
        )
    }
}
