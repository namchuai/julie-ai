package ai.julie.feature.promptlab.model

import kotlinx.datetime.Instant

data class TestSession(
    val id: String,
    val name: String,
    val templateId: String,
    val versionId: String,
    val models: List<String>,
    val variableValues: Map<String, String>,
    val testResults: List<TestResult>,
    val createdAt: Instant
)

data class TestResult(
    val modelId: String,
    val response: String,
    val latencyMs: Long,
    val tokenCount: Int,
    val evaluations: Map<String, Double> // criteriaId -> score
)