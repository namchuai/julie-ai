package ai.julie.feature.promptlab.ui.project

import ai.julie.core.designsystem.component.components.Scaffold
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.designsystem.component.components.topbar.TopBar
import ai.julie.feature.promptlab.model.Project
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
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
import org.koin.core.parameter.parametersOf

@Composable
fun ProjectListScreen(
    workspaceId: String,
    onNavigateToPrompts: (projectId: String, projectName: String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ProjectListViewModel = koinViewModel { parametersOf(workspaceId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopBar {
                Text("Projects")
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateDialog() }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Project")
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

                uiState.projects.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.align(Alignment.Center),
                        onCreateClick = { viewModel.showCreateDialog() }
                    )
                }

                else -> {
                    ProjectList(
                        projects = uiState.projects,
                        onProjectClick = onNavigateToPrompts
                    )
                }
            }
        }
    }

    if (uiState.showCreateDialog) {
        CreateProjectDialog(
            onDismiss = { viewModel.hideCreateDialog() },
            onCreate = { name, description, tags ->
                viewModel.createProject(name, description, tags)
            }
        )
    }
}

@Composable
private fun ProjectList(
    projects: List<Project>,
    onProjectClick: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(projects) { project ->
            ProjectCard(
                project = project,
                onClick = { onProjectClick(project.id, project.name) }
            )
        }
    }
}

@Composable
private fun ProjectCard(
    project: Project,
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
                text = project.name,
                style = MaterialTheme.typography.titleMedium
            )

            project.description?.let { desc ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (project.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    project.tags.forEach { tag ->
                        AssistChip(
                            onClick = { },
                            label = { Text(tag) },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
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
            text = "No projects yet",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your first project to organize prompts",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCreateClick) {
            Text("Create Project")
        }
    }
}