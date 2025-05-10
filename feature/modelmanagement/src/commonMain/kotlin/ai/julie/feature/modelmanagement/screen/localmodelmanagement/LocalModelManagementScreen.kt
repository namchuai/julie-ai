package ai.julie.feature.modelmanagement.screen.localmodelmanagement

import ai.julie.core.designsystem.component.components.Scaffold
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.designsystem.component.components.topbar.TopBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LocalModelManagementScreenRoute(
    viewModel: LocalModelManagementViewModel,
    onBackClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    return LocalModelManagementScreen(
        models = state.models,
    )
}

@Composable
fun LocalModelManagementScreen(
    models: List<String> = emptyList(),
) {
    Scaffold(
        topBar = {
            TopBar {
                Text("Local Model Management")
            }
        },
        content = { padding ->
            LazyColumn(
                contentPadding = padding
            ) {
                items(models) { model ->
                    ModelItem(model)
                }
            }
        })
}

@Composable
internal fun ModelItem(
    modelName: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = modelName)
    }
}