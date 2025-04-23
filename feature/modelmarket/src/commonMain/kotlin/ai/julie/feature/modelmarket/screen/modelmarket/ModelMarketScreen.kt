package ai.julie.feature.modelmarket.screen.modelmarket

import ai.julie.core.designsystem.component.components.Button
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.designsystem.component.components.textfield.TextField
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun ModelMarketScreenRoute(
    viewModel: ModelMarketViewModel,
    onBackClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    return ModelMarketScreen(
        downloadUrl = state.downloadUrl,
        onDownloadUrlUpdate = viewModel::onDownloadUrlUpdate,
        onStartDownloadClick = viewModel::onStartDownloadClick,
        onBackClick = onBackClick,
    )
}

@Composable
fun ModelMarketScreen(
    downloadUrl: String,
    onDownloadUrlUpdate: (String) -> Unit,
    onStartDownloadClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Button(onClick = onBackClick) {
            Text("Back")
        }
        TextField(
            value = downloadUrl,
            onValueChange = onDownloadUrlUpdate,
        )

        Button(
            onClick = onStartDownloadClick,
        ) {
            Text("Download")
        }
    }
}
