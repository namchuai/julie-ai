package ai.julie.core.model.chatcompletion.create.message

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Deprecated("Use ToolMessageRequest instead")
@Serializable
data class FunctionMessageRequest(
    @SerialName("content") val content: String?,
    @SerialName("name") override val name: String,
) : MessageRequest() {
    @Required
    @SerialName("role")
    override val role: String = "function"
}
