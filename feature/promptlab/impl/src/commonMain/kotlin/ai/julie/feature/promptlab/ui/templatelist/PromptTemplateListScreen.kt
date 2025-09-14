package ai.julie.feature.promptlab.ui.templatelist

import ai.julie.core.common.AsyncState
import ai.julie.core.designsystem.component.components.Scaffold
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.designsystem.component.components.topbar.TopBar
import ai.julie.feature.promptlab.model.PromptTemplate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PromptTemplateListScreen(
    projectId: String,
    projectName: String,
    onNavigateToTemplate: (templateId: String?) -> Unit, // null for new template
    onNavigateToTest: (templateId: String) -> Unit,
    onNavigateToHistory: (templateId: String) -> Unit,
    viewModel: PromptTemplateListViewModel = koinViewModel { parametersOf(projectId) }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopBar {
                Text("Templates - $projectName")
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToTemplate(null) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Template")
            }
        }
    ) {
        when (state) {
            is AsyncState.Success -> {
                val successState = state as AsyncState.Success<PromptTemplateListUiState>
                if (successState.value.templates.isEmpty()) {
                    EmptyTemplateState(
                        onCreateClick = { onNavigateToTemplate(null) }
                    )
                } else {
                    TemplateList(
                        templates = successState.value.templates,
                        onTemplateClick = { onNavigateToTemplate(it.id) },
                        onTestClick = onNavigateToTest,
                        onHistoryClick = onNavigateToHistory
                    )
                }
            }

            is AsyncState.Loading -> {
                CircularProgressIndicator()
            }

            else -> {}
        }
    }
}

@Composable
private fun TemplateList(
    templates: List<PromptTemplate>,
    onTemplateClick: (PromptTemplate) -> Unit,
    onTestClick: (String) -> Unit,
    onHistoryClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(templates) { template ->
            TemplateCard(
                template = template,
                onClick = { onTemplateClick(template) },
                onTestClick = { onTestClick(template.id) },
                onHistoryClick = { onHistoryClick(template.id) }
            )
        }
    }
}

@Composable
private fun TemplateCard(
    template: PromptTemplate,
    onClick: () -> Unit,
    onTestClick: () -> Unit,
    onHistoryClick: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = template.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Updated ${formatDate(template.updatedAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { onTestClick() }
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Test",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { onHistoryClick() }
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Preview of content
            if (template.content.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = template.content.take(100) + if (template.content.length > 100) "..." else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            // Variables count
//            if (template.variables.isNotEmpty()) {
//                Spacer(modifier = Modifier.height(8.dp))
//                Surface(
//                    color = MaterialTheme.colorScheme.secondaryContainer,
//                    shape = MaterialTheme.shapes.small
//                ) {
//                    Text(
//                        text = "${template.variables.size} variables",
//                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
//                        style = MaterialTheme.typography.labelSmall,
//                        color = MaterialTheme.colorScheme.onSecondaryContainer
//                    )
//                }
//            }
        }
    }
}

@Composable
private fun EmptyTemplateState(
    modifier: Modifier = Modifier,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No templates yet",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your first prompt template to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCreateClick) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Template")
        }
    }
}

private fun formatDate(instant: kotlinx.datetime.Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.monthNumber}/${localDateTime.dayOfMonth}/${localDateTime.year}"
}