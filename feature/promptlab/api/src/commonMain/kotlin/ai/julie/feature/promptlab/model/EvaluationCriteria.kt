package ai.julie.feature.promptlab.model

import kotlinx.datetime.Instant

data class EvaluationCriteria(
    val id: String,
    val projectId: String,
    val name: String,
    val description: String?,
    val type: EvaluationType,
    val config: EvaluationConfig,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class EvaluationType {
    BINARY, // Yes/No
    SCALE, // 1-5, 1-10, etc.
    PERCENTAGE, // 0-100%
    CUSTOM // Custom scoring logic
}

data class EvaluationConfig(
    val minValue: Double? = null,
    val maxValue: Double? = null,
    val labels: Map<String, String>? = null // For scale types
)