package ai.julie.feature.modelmanagement.navigation

import androidx.navigation.NavGraphBuilder

/**
 * Abstract navigation interface for model management features.
 * Implementation is provided by the impl module.
 */
interface ModelManagementNavigation {
    /**
     * Adds model management navigation to the NavGraphBuilder.
     */
    fun NavGraphBuilder.modelManagementGraph(
        onModelClick: () -> Unit = {},
        onNavigateToChat: () -> Unit = {},
        onBackClick: () -> Unit,
    )
}

/**
 * Extension function to easily add model management navigation.
 * Requires the ModelManagementNavigation implementation to be available in DI.
 */
fun NavGraphBuilder.modelManagementGraph(
    navigation: ModelManagementNavigation,
    onModelClick: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onBackClick: () -> Unit,
) {
    with(navigation) {
        modelManagementGraph(
            onModelClick = onModelClick,
            onNavigateToChat = onNavigateToChat,
            onBackClick = onBackClick,
        )
    }
}