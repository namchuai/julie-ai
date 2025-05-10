package ai.julie.feature.promptlab.model

import kotlinx.datetime.Instant

data class PromptVersion(
    val id: String,
    val templateId: String,
    val versionNumber: Int,
    val content: String,
    val variables: Map<String, VariableConfig>,
    val commitMessage: String,
    val authorId: String,
    val createdAt: Instant
)