package ai.julie.feature.modelmanagement.ui.localmodelmanagement

import ai.julie.core.designsystem.component.components.Button
import ai.julie.core.designsystem.component.components.Scaffold
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.designsystem.component.components.topbar.TopBar
import ai.julie.core.model.aimodel.LocalModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LocalModelManagementScreenRoute(
    onNavigateToChat: () -> Unit = {},
    onBackClick: () -> Unit,
) {
    val viewModel = koinViewModel<LocalModelManagementViewModel>()
    val state by viewModel.state.collectAsState()
    return LocalModelManagementScreen(
        state = state,
        onAddLocalModelClick = viewModel::onAddLocalModelClick,
        onDeleteModel = viewModel::onDeleteModel,
        onRunModel = { model ->
            viewModel.onRunModelAndNavigateToChat(model, onNavigateToChat)
        },
    )
}

@Composable
fun LocalModelManagementScreen(
    state: LocalModelManagementState,
    onAddLocalModelClick: () -> Unit = {},
    onDeleteModel: (String) -> Unit = {},
    onRunModel: (LocalModel) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopBar {
                Text("Local Models")
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Add Model Button
                Button(
                    onClick = onAddLocalModelClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Add Local Model")
                }

                // Models List
                if (state.models.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No models added yet",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Click 'Add Local Model' to import your first model",
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.models) { model ->
                            ModelItem(
                                model = model,
                                onDeleteModel = { onDeleteModel(model.id) },
                                onRunModel = { onRunModel(model) },
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun ModelItem(
    model: LocalModel,
    onDeleteModel: () -> Unit,
    onRunModel: (LocalModel) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = model.title.takeIf { it.isNotBlank() } ?: "Unnamed Model",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                        maxLines = 1

                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = model.description.takeIf { it.isNotBlank() } ?: "No description available",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                        maxLines = 3
                    )
                }

                IconButton(onClick = onDeleteModel) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colors.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button
            Button(
                onClick = { onRunModel(model) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Start Chat"
                )
            }
        }
    }
}
