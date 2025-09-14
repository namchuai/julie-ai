package ai.julie.feature.promptlab.model

import kotlinx.datetime.Instant

data class Workspace(
    val id: String,
    val name: String,
    val type: WorkspaceType,
    val owner: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class WorkspaceType {
    LOCAL,
    CLOUD
}