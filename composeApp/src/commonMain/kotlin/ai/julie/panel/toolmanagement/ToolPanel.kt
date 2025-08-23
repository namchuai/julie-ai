package ai.julie.panel.toolmanagement

import ai.julie.feature.toolmanagement.ui.ToolListingScreenRoute
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ToolPanel(
    modifier: Modifier = Modifier
) {
    ToolListingScreenRoute(
        onNavigateBack = { /* Root level, no back action */ }
    )
}