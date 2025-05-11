package ai.julie.feature.modelmanagement.screen.localmodelmanagement

import ai.julie.core.designsystem.component.components.Button
import ai.julie.core.designsystem.component.components.Scaffold
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.designsystem.component.components.topbar.TopBar
import ai.julie.core.model.aimodel.AiModel
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
    onModelClick: (AiModel) -> Unit = {},
    onBackClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    return LocalModelManagementScreen(
        models = state.models,
        onModelClick = onModelClick,
    )
}

@Composable
fun LocalModelManagementScreen(
    models: List<AiModel> = emptyList(),
    onModelClick: (AiModel) -> Unit = {},
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
                    ModelItem(model, onModelClick)
                }
            }
        })
}

@Composable
internal fun ModelItem(
    model: AiModel,
    onModelClick: (AiModel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(text = model.title)
        Button(
            onClick = { onModelClick(model) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = "Use this model")
        }
    }
}