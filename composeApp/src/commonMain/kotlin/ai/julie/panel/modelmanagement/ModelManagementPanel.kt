package ai.julie.panel.modelmanagement

import ai.julie.feature.modelmanagement.ui.localmodelmanagement.LocalModelManagementScreenRoute
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ModelManagementPanel(
    modifier: Modifier = Modifier,
    onNavigateToChat: () -> Unit = {},
) {
    LocalModelManagementScreenRoute(
        onNavigateToChat = onNavigateToChat,
        onBackClick = {},
    )
}
