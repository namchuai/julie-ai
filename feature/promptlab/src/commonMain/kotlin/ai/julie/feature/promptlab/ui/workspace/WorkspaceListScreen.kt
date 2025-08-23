package ai.julie.feature.promptlab.ui.workspace

import ai.julie.core.designsystem.component.components.Scaffold
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.designsystem.component.components.topbar.TopBar
import ai.julie.feature.promptlab.model.Workspace
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorkspaceListScreen(
    onNavigateToProjects: (workspaceId: String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: WorkspaceListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopBar {
                Text("Prompt Lab")
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateDialog() }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Workspace")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.workspaces.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.align(Alignment.Center),
                        onCreateClick = { viewModel.showCreateDialog() }
                    )
                }

                else -> {
                    WorkspaceList(
                        workspaces = uiState.workspaces,
                        onWorkspaceClick = onNavigateToProjects
                    )
                }
            }
        }
    }

    if (uiState.showCreateDialog) {
        CreateWorkspaceDialog(
            onDismiss = { viewModel.hideCreateDialog() },
            onCreate = { name, type ->
                viewModel.createWorkspace(name, type)
            }
        )
    }
}

@Composable
private fun WorkspaceList(
    workspaces: List<Workspace>,
    onWorkspaceClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(workspaces) { workspace ->
            WorkspaceCard(
                workspace = workspace,
                onClick = { onWorkspaceClick(workspace.id) }
            )
        }
    }
}

@Composable
private fun WorkspaceCard(
    workspace: Workspace,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = workspace.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Type: ${workspace.type.name}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No workspaces yet",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your first workspace to get started",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCreateClick) {
            Text("Create Workspace")
        }
    }
}