package ai.julie.feature.promptlab.model

import kotlinx.datetime.Instant

data class PromptTemplateContext(
    val id: String,
    val promptTemplateId: String,
    val variables: Map<String, String>,
    val createdAt: Instant,
    val updatedAt: Instant
)
