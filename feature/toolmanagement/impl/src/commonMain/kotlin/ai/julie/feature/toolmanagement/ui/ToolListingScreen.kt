package ai.julie.feature.toolmanagement.ui

import ai.julie.core.common.AsyncState
import ai.julie.core.designsystem.component.components.Scaffold
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.designsystem.component.components.topbar.TopBar
import ai.julie.feature.toolmanagement.domain.EnrichedTool
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ToolListingScreenRoute(
    onNavigateBack: () -> Unit = {},
) {
    val viewModel = koinViewModel<ToolListingViewModel>()
    val state by viewModel.state.collectAsState()
    
    var showCreateDialog by remember { mutableStateOf(false) }

    ToolListingContent(
        state = state,
        onCreateClick = { showCreateDialog = true },
        onDeleteTool = viewModel::onDeleteTool
    )
    
    if (showCreateDialog) {
        CreateToolDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { tool ->
                viewModel.onCreateTool(tool)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun ToolListingContent(
    state: AsyncState<ToolListingState>,
    onCreateClick: () -> Unit,
    onDeleteTool: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopBar {
                Text("Tool Management")
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateClick
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Tool")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is AsyncState.Idle -> {
                    // Initial state - could show empty or loading
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is AsyncState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is AsyncState.Success -> {
                    if (state.value.tools.isEmpty()) {
                        EmptyState(
                            modifier = Modifier.align(Alignment.Center),
                            onCreateClick = onCreateClick
                        )
                    } else {
                        ToolList(
                            tools = state.value.tools,
                            onDeleteTool = onDeleteTool
                        )
                    }
                }
                
                is AsyncState.Failure -> {
                    Text(
                        text = "Error loading tools",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                else -> {
                    // Fallback for any other states
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolList(
    tools: List<EnrichedTool>,
    onDeleteTool: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tools) { tool ->
            ToolCard(
                tool = tool,
                onDeleteClick = { onDeleteTool(tool.function.name) }
            )
        }
    }
}

@Composable
private fun ToolCard(
    tool: EnrichedTool,
    onDeleteClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tool.function.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Tool",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Text(
                text = tool.function.description ?: "No description",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Category: ${tool.category.name}",
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
            text = "No tools yet",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your first tool to get started",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCreateClick) {
            Text("Create Tool")
        }
    }
}

