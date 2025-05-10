package ai.julie.core.model.aimodel

import kotlinx.serialization.Serializable

@Serializable
sealed interface AiModel {
    val id: String
    val title: String
}
