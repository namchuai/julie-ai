package ai.julie.core.model.chatcompletion.create.message

import kotlinx.serialization.Serializable

@Serializable
sealed class MessageRequest {
    abstract val role: String
    abstract val name: String?
}
