package ai.julie.core.network.model.createchatcompletion.response

import ai.julie.core.model.chatcompletionresponse.ResponseMessage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(

    /**
     * The role of the author of this message.
     */
    @SerialName("role") val role: String,

    /**
     * The contents of the message.
     */
    @SerialName("content") val content: String? = null,

    /**
     * The refusal message generated by the model.
     */
    @SerialName("refusal") val refusal: String? = null,

    /**
     * Annotations for the message, when applicable, as when using the web search tool.
     */
    @SerialName("annotations") val annotations: List<Annotation>? = null,

    /**
     * If the audio output modality is requested, this object contains data about the audio response
     * from the model. [Learn more](https://platform.openai.com/docs/guides/audio)
     */
    @SerialName("audio") val audio: Audio? = null,

    /**
     * The tool calls generated by the model, such as function calls.
     */
    @SerialName("tool_calls") val toolCalls: List<ToolCall>? = null,

    /**
     * Deprecated and replaced by tool_calls. The name and arguments of a function that should be
     * called, as generated by the model.
     */
    @SerialName("function_call") @Deprecated("Use tool_calls") val functionCall: FunctionCall? = null,
)

@Serializable
data class Audio(

    /**
     * Base64 encoded audio bytes generated by the model, in the format specified in the request.
     */
    @SerialName("data") val data: String,

    /**
     * The Unix timestamp (in seconds) for when this audio response will no longer be accessible on
     * the server for use in multi-turn conversations.
     */
    @SerialName("expires_at") val expiresAt: Int,

    /**
     * Unique identifier for this audio response.
     */
    @SerialName("id") val id: String,

    /**
     * Transcript of the audio generated by the model.
     */
    @SerialName("transcript") val transcript: String,
)

@Serializable
data class ToolCall(

    /**
     * The ID of the tool call.
     */
    @SerialName("id") val id: String,

    /**
     * The type of the tool. Currently, only function is supported.
     */
    @SerialName("type") val type: String,

    /**
     * The function that the model called.
     */
    @SerialName("function") val function: Function,
)

@Serializable
data class Function(

    /**
     * The arguments to call the function with, as generated by the model in JSON format. Note that
     * the model does not always generate valid JSON, and may hallucinate parameters not defined by
     * your function schema. Validate the arguments in your code before calling your function.
     */
    @SerialName("arguments") val arguments: String,

    /**
     * The name of the function to call.
     */
    @SerialName("name") val name: String,
)

@Serializable
data class FunctionCall(
    /**
     * The name of the function to call.
     */
    @SerialName("name") val name: String,

    /**
     * The arguments to call the function with, as generated by the model in JSON format. Note that
     * the model does not always generate valid JSON, and may hallucinate parameters not defined by
     * your function schema. Validate the arguments in your code before calling your function.
     */
    @SerialName("arguments") val arguments: String,
)

@Serializable
data class Annotation(

    /**
     * The type of the URL citation. Always url_citation.
     */
    @SerialName("type") val type: String,

    /**
     * A URL citation when using web search.
     */
    @SerialName("url_citation") val urlCitation: UrlCitation,
)

@Serializable
data class UrlCitation(

    /**
     * The index of the last character of the URL citation in the message.
     */
    @SerialName("end_index") val endIndex: Int,

    /**
     * The index of the first character of the URL citation in the message.
     */
    @SerialName("start_index") val startIndex: Int,

    /**
     * The title of the web resource.
     */
    @SerialName("title") val title: String,

    /**
     * The URL of the web resource.
     */
    @SerialName("url") val url: String,
)

fun Message.toResponseMessage() = ResponseMessage(
    role = this.role,
    content = this.content,
    refusal = this.refusal,
//    annotations = this.annotations, // TODO: NamH
//    audio = this.audio,
    toolCalls = null, //this.toolCalls,
    functionCall = null, //this.functionCall,
)
