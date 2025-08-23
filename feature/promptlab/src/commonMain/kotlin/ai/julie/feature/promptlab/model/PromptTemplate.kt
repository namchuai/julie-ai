package ai.julie.feature.promptlab.model

import kotlinx.datetime.Instant

data class PromptTemplate(
    val id: String,
    val projectId: String,
    val name: String,
    val content: String,
    val variables: Map<String, VariableConfig>,
    val currentVersion: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class VariableConfig(
    val type: VariableType,
    val description: String?,
    val defaultValue: String?,
    val options: List<String>? = null // For enum types
)

enum class VariableType {
    STRING,
    NUMBER,
    BOOLEAN,
    ENUM
}