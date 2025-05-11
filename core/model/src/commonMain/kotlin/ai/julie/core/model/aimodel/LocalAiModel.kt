package ai.julie.core.model.aimodel

import kotlinx.serialization.Serializable

@Serializable
data class LocalAiModel(
    override val id: String,
    override val title: String,
    val description: String,
    val localPath: String? = null,
) : AiModel
