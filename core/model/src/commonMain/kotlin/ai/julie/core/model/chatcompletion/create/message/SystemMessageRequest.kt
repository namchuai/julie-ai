package ai.julie.core.model.chatcompletion.create.message

import ai.julie.core.model.chatcompletion.create.message.content.MessageContentRequest
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SystemMessageRequest(
    @SerialName("content") val content: MessageContentRequest,
    @SerialName("name") override val name: String? = null,
) : MessageRequest() {
    @Required
    @SerialName("role")
    override val role: String = "system"
}
