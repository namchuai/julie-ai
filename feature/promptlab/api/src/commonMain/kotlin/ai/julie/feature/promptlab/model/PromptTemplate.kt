package ai.julie.feature.promptlab.model

import kotlinx.datetime.Instant

data class PromptTemplate(
    val id: String,
    val name: String,
    val content: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
