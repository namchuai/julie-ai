package ai.julie.panel.promptlab

import ai.julie.feature.promptlab.ui.project.ProjectListScreen
import ai.julie.feature.promptlab.ui.prompttemplate.PromptTemplateScreen
import ai.julie.feature.promptlab.ui.templatelist.PromptTemplateListScreen
import ai.julie.feature.promptlab.ui.test.TestSessionScreen
import ai.julie.feature.promptlab.ui.workspace.WorkspaceListScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

sealed class PromptLabRoute {
    object WorkspaceList : PromptLabRoute()
    data class ProjectList(val workspaceId: String) : PromptLabRoute()
    data class TemplateList(val projectId: String, val projectName: String) : PromptLabRoute()
    data class PromptTemplate(val projectId: String, val templateId: String? = null) :
        PromptLabRoute()

    data class TestSession(val templateId: String) : PromptLabRoute()
    data class VersionHistory(val templateId: String) : PromptLabRoute()
}

@Composable
fun PromptLabPanel(
    modifier: Modifier = Modifier
) {
    var currentRoute by remember { mutableStateOf<PromptLabRoute>(PromptLabRoute.WorkspaceList) }

    when (val route = currentRoute) {
        is PromptLabRoute.WorkspaceList -> {
            WorkspaceListScreen(
                onNavigateToProjects = { workspaceId ->
                    currentRoute = PromptLabRoute.ProjectList(workspaceId)
                },
                onNavigateBack = { /* Root level, no back action */ }
            )
        }

        is PromptLabRoute.ProjectList -> {
            ProjectListScreen(
                workspaceId = route.workspaceId,
                onNavigateToPrompts = { projectId, projectName ->
                    currentRoute = PromptLabRoute.TemplateList(projectId, projectName)
                },
                onNavigateBack = {
                    currentRoute = PromptLabRoute.WorkspaceList
                }
            )
        }

        is PromptLabRoute.TemplateList -> {
            PromptTemplateListScreen(
                projectId = route.projectId,
                projectName = route.projectName,
                onNavigateBack = {
                    currentRoute = PromptLabRoute.ProjectList(route.projectId)
                },
                onNavigateToTemplate = { templateId ->
                    currentRoute = PromptLabRoute.PromptTemplate(route.projectId, templateId)
                },
                onNavigateToTest = { templateId ->
                    currentRoute = PromptLabRoute.TestSession(templateId)
                },
                onNavigateToHistory = { templateId ->
                    currentRoute = PromptLabRoute.VersionHistory(templateId)
                }
            )
        }

        is PromptLabRoute.PromptTemplate -> {
            PromptTemplateScreen(
                projectId = route.projectId,
                templateId = route.templateId,
                onNavigateBack = {
                    currentRoute = PromptLabRoute.TemplateList(route.projectId, "Project")
                },
                onNavigateToTest = { templateId ->
                    currentRoute = PromptLabRoute.TestSession(templateId)
                },
                onNavigateToHistory = { templateId ->
                    currentRoute = PromptLabRoute.VersionHistory(templateId)
                }
            )
        }

        is PromptLabRoute.TestSession -> {
            TestSessionScreen(
                templateId = route.templateId,
                onNavigateBack = {
                    // For now, go back to workspace list since we don't have the projectId
                    // TODO: Store projectId in TestSession route
                    currentRoute = PromptLabRoute.WorkspaceList
                }
            )
        }

        is PromptLabRoute.VersionHistory -> {
            // TODO: Implement version history screen
            // For now, show workspace list
            WorkspaceListScreen(
                onNavigateToProjects = { workspaceId ->
                    currentRoute = PromptLabRoute.ProjectList(workspaceId)
                },
                onNavigateBack = {
                    currentRoute = PromptLabRoute.WorkspaceList
                }
            )
        }
    }
}