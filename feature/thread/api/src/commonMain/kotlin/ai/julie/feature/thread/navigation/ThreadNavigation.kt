package ai.julie.feature.thread.navigation

import androidx.navigation.NavGraphBuilder

/**
 * Abstract navigation interface for thread features.
 * Implementation is provided by the impl module.
 */
interface ThreadNavigation {
    /**
     * Adds thread navigation to the NavGraphBuilder.
     */
    fun NavGraphBuilder.threadGraph(
        onBackClick: () -> Unit,
    )
}

/**
 * Extension function to easily add thread navigation.
 * Requires the ThreadNavigation implementation to be available in DI.
 */
fun NavGraphBuilder.threadGraph(
    navigation: ThreadNavigation,
    onBackClick: () -> Unit,
) {
    with(navigation) {
        threadGraph(
            onBackClick = onBackClick,
        )
    }
}