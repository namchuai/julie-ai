package ai.julie.core.model.chatcompletion.create.message

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ToolMessageRequest(
    @SerialName("content") val content: JsonElement,
    @SerialName("tool_call_id") val toolCallId: String,
    @SerialName("name") override val name: String? = null,
) : MessageRequest() {
    @Required
    @SerialName("role")
    override val role: String = "tool"
}
