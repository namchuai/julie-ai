package ai.julie.feature.promptlab.ui.prompttemplate

import ai.julie.core.designsystem.component.components.Scaffold
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.designsystem.component.components.textfield.TextField
import ai.julie.core.designsystem.component.components.topbar.TopBar
import ai.julie.feature.promptlab.model.VariableConfig
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptTemplateScreen(
    projectId: String,
    templateId: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToTest: (templateId: String) -> Unit,
    onNavigateToHistory: (templateId: String) -> Unit,
    viewModel: PromptTemplateViewModel = koinViewModel { parametersOf(projectId, templateId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigate back after successful save
    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopBar {
                Text(if (templateId == null) "New Prompt" else "Edit Prompt")
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.saveTemplate() },
                containerColor = if (uiState.canSaveComputed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = "Save",
                    tint = if (uiState.canSaveComputed) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Template Name
            TextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text("Template Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Prompt Content
            TextField(
                value = uiState.content,
                onValueChange = viewModel::updateContent,
                label = { Text("Prompt Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                placeholder = {
                    Text("Enter your prompt here. Use {{variable}} for variables.")
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Variables Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Variables",
                    style = MaterialTheme.typography.titleMedium
                )

                TextButton(onClick = viewModel::addVariable) {
                    Text("Add Variable")
                }
            }

            if (uiState.variables.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "No variables defined. Use {{variableName}} in your prompt to create variables.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                uiState.variables.forEach { (name, config) ->
                    VariableConfigCard(
                        name = name,
                        config = config,
                        onConfigChange = { viewModel.updateVariable(name, it) },
                        onDelete = { viewModel.removeVariable(name) }
                    )
                }
            }

            // Action Buttons (only show if template exists)
            if (templateId != null) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onNavigateToTest(templateId) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Test")
                    }

                    OutlinedButton(
                        onClick = { onNavigateToHistory(templateId) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("History")
                    }
                }
            }

            // Preview Section
            if (uiState.previewValues.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Preview",
                    style = MaterialTheme.typography.titleMedium
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = viewModel.getPreview(),
                        modifier = Modifier.padding(16.dp),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }

    if (uiState.showAddVariableDialog) {
        AddVariableDialog(
            onDismiss = viewModel::hideAddVariableDialog,
            onAdd = viewModel::addVariableWithConfig
        )
    }

    // Show error message if any
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // TODO: Show snackbar or alert dialog with error message
            println("Error: $error")
        }
    }
}

@Composable
private fun VariableConfigCard(
    name: String,
    config: VariableConfig,
    onConfigChange: (VariableConfig) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "{{$name}}",
                    style = MaterialTheme.typography.titleSmall,
                    fontFamily = FontFamily.Monospace
                )

                TextButton(onClick = onDelete) {
                    Text("Remove")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Variable configuration UI
            TextField(
                value = config.description ?: "",
                onValueChange = {
                    onConfigChange(config.copy(description = it))
                },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            // Add more configuration options based on variable type
        }
    }
}