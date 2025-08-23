package ai.julie.feature.promptlab.usecase

import ai.julie.feature.promptlab.model.Project
import ai.julie.feature.promptlab.repository.ProjectRepository
import kotlinx.datetime.Clock
import kotlin.uuid.Uuid

class CreateProjectUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(
        workspaceId: String,
        name: String,
        description: String? = null,
        tags: List<String> = emptyList()
    ): Result<Project> = runCatching {
        val now = Clock.System.now()
        val project = Project(
            id = Uuid.random().toHexString(),
            workspaceId = workspaceId,
            name = name,
            description = description,
            tags = tags,
            createdAt = now,
            updatedAt = now
        )
        projectRepository.create(project)
    }
}