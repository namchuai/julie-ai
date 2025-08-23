package ai.julie.core.model.aimodel

import kotlinx.serialization.Serializable

@Serializable
data class RemoteModel(
    override val id: String,
    override val title: String,
    val provider: String,
) : AiModel