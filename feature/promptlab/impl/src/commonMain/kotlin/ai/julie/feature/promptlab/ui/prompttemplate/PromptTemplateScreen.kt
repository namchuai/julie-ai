package ai.julie.feature.promptlab.ui.prompttemplate

import ai.julie.core.designsystem.component.components.Scaffold
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.designsystem.component.components.topbar.TopBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptTemplateScreen(
    projectId: String,
    templateId: String? = null,
    onNavigateToTest: (templateId: String) -> Unit,
    onNavigateToHistory: (templateId: String) -> Unit,
    viewModel: PromptTemplateViewModel = koinViewModel { parametersOf(projectId, templateId) }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopBar {
                Text(if (templateId == null) "New Prompt" else "Edit Prompt")
            }
        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { viewModel.saveTemplate() },
//                containerColor = if (uiState.canSaveComputed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
//            ) {
//                Icon(
//                    Icons.Default.Save,
//                    contentDescription = "Save",
//                    tint = if (uiState.canSaveComputed) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//        }
    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .verticalScroll(rememberScrollState())
//                .padding(16.dp)
//        ) {
//            // Template Name
//            TextField(
//                value = uiState.name,
//                onValueChange = viewModel::updateName,
//                label = { Text("Template Name") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Prompt Content
//            TextField(
//                value = uiState.content,
//                onValueChange = viewModel::updateContent,
//                label = { Text("Prompt Content") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .heightIn(min = 200.dp),
//                placeholder = {
//                    Text("Enter your prompt here. Use {{variable}} for variables.")
//                }
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Variables Section
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Variables",
//                    style = MaterialTheme.typography.titleMedium
//                )
//
//                TextButton(onClick = viewModel::addVariable) {
//                    Text("Add Variable")
//                }
//            }
//
//            if (uiState.variables.isEmpty()) {
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp)
//                ) {
//                    Text(
//                        text = "No variables defined. Use {{variableName}} in your prompt to create variables.",
//                        modifier = Modifier.padding(16.dp),
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            } else {
//                uiState.variables.forEach { (name, config) ->
//                    VariableConfigCard(
//                        name = name,
//                        config = config,
//                        onConfigChange = { viewModel.updateVariable(name, it) },
//                        onDelete = { viewModel.removeVariable(name) }
//                    )
//                }
//            }
//
//            // Action Buttons (only show if template exists)
//            if (templateId != null) {
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    OutlinedButton(
//                        onClick = { onNavigateToTest(templateId) },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Icon(
//                            Icons.Default.PlayArrow,
//                            contentDescription = null,
//                            modifier = Modifier.size(18.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text("Test")
//                    }
//
//                    OutlinedButton(
//                        onClick = { onNavigateToHistory(templateId) },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Icon(
//                            Icons.Default.History,
//                            contentDescription = null,
//                            modifier = Modifier.size(18.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text("History")
//                    }
//                }
//            }
//
//            // Preview Section
//            if (uiState.previewValues.isNotEmpty()) {
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Text(
//                    text = "Preview",
//                    style = MaterialTheme.typography.titleMedium
//                )
//
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp)
//                ) {
//                    Text(
//                        text = viewModel.getPreview(),
//                        modifier = Modifier.padding(16.dp),
//                        fontFamily = FontFamily.Monospace
//                    )
//                }
//            }
//        }
    }

//    if (uiState.showAddVariableDialog) {
//        AddVariableDialog(
//            onDismiss = viewModel::hideAddVariableDialog,
//            onAdd = viewModel::addVariableWithConfig
//        )
//    }
}

//@Composable
//private fun VariableConfigCard(
//    name: String,
//    config: VariableConfig,
//    onConfigChange: (VariableConfig) -> Unit,
//    onDelete: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = "{{$name}}",
//                    style = MaterialTheme.typography.titleSmall,
//                    fontFamily = FontFamily.Monospace
//                )
//
//                TextButton(onClick = onDelete) {
//                    Text("Remove")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Variable configuration UI
//            TextField(
//                value = config.description ?: "",
//                onValueChange = {
//                    onConfigChange(config.copy(description = it))
//                },
//                label = { Text("Description") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            // Add more configuration options based on variable type
//        }
//    }
//}
