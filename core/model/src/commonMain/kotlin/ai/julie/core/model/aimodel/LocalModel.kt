package ai.julie.core.model.aimodel

import kotlinx.serialization.Serializable

@Serializable
data class LocalModel(
    override val id: String,
    override val title: String,
    val description: String,
    val localPath: String? = null, // TODO: If it's the local model, it should have path? why nullable?
    // TODO: add hashing here to verify the integrity of the local model
) : AiModel
