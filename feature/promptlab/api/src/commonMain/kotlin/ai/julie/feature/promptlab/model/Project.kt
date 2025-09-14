package ai.julie.feature.promptlab.model

import kotlinx.datetime.Instant

data class Project(
    val id: String,
    val workspaceId: String,
    val name: String,
    val description: String?,
    val tags: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant
)