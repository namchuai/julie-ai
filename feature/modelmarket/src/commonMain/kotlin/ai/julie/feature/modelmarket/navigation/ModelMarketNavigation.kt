package ai.julie.feature.modelmarket.navigation

import ai.julie.feature.modelmarket.screen.modelmarket.ModelMarketScreenRoute
import ai.julie.feature.modelmarket.screen.modelmarket.ModelMarketViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
internal data object ModelMarketScreen

fun NavController.navigateToModelMarket() = navigate(route = ModelMarketScreen)

fun NavGraphBuilder.modelMarketGraph(
    onBackClick: () -> Unit,
) {
    composable<ModelMarketScreen> {
        val viewModel = koinViewModel<ModelMarketViewModel>()
        ModelMarketScreenRoute(viewModel, onBackClick)
    }
}