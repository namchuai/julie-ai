package ai.julie.core.model.aimodel

import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
sealed interface AiModel {
    val id: String
    val title: String
}

val aiModelSerializersModule = SerializersModule {
    polymorphic(AiModel::class) {
        subclass(LocalAiModel::class)
        subclass(RemoteAiModel::class)
        // TODO: Add any other direct subtypes of AiModel here if they exist
    }
}
