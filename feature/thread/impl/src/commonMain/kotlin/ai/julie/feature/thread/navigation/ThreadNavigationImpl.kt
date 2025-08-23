package ai.julie.feature.thread.navigation

import ai.julie.feature.thread.ui.threadlisting.ThreadListingScreenRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ai.julie.feature.thread.navigation.ThreadNavigation as ThreadNavigationInterface

/**
 * Implementation of ThreadNavigation interface.
 */
class ThreadNavigationImpl : ThreadNavigationInterface {

    override fun NavGraphBuilder.threadGraph(
        onBackClick: () -> Unit,
    ) {
        composable("threadListing") {
            ThreadListingScreenRoute(
                onBackClick = onBackClick,
            )
        }
    }
}