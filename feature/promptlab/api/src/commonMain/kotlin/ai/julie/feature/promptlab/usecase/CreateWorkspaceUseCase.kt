package ai.julie.feature.promptlab.usecase

import ai.julie.feature.promptlab.model.Workspace
import ai.julie.feature.promptlab.model.WorkspaceType
import ai.julie.feature.promptlab.repository.WorkspaceRepository
import kotlinx.datetime.Clock
import kotlin.uuid.Uuid

class CreateWorkspaceUseCase(
    private val workspaceRepository: WorkspaceRepository
) {
    suspend operator fun invoke(
        name: String,
        type: WorkspaceType = WorkspaceType.LOCAL,
        owner: String = "default_user" // TODO: Get from auth
    ): Result<Workspace> = runCatching {
        val now = Clock.System.now()
        val workspace = Workspace(
            id = Uuid.random().toHexString(),
            name = name,
            type = type,
            owner = owner,
            createdAt = now,
            updatedAt = now
        )
        workspaceRepository.create(workspace)
    }
}