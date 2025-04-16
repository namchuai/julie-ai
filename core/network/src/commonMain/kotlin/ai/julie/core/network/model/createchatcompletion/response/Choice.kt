package ai.julie.core.network.model.createchatcompletion.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ai.julie.core.model.chatcompletionresponse.Choice as ModelChoice

@Serializable
data class Choice(

    /**
     * The reason the model stopped generating tokens. This will be stop if the model hit a natural
     * stop point or a provided stop sequence, length if the maximum number of tokens specified in
     * the request was reached, content_filter if content was omitted due to a flag from our content
     * filters, tool_calls if the model called a tool, or function_call (deprecated) if the model
     * called a function.
     */
    @SerialName("finish_reason") val finishReason: String,

    /**
     * The index of the choice in the list of choices.
     */
    @SerialName("index") val index: Int,

    /**
     * A chat completion message generated by the model.
     */
    @SerialName("message") val message: Message,

    /**
     * Log probability information for the choice.
     */
    @SerialName("logprobs") val logprobs: Logprobs? = null,
)

fun Choice.toChoice() = ModelChoice(
    finishReason = this.finishReason,
    index = this.index,
    message = this.message.toResponseMessage(),
//    logprobs = this.logprobs?.toLogprobs() // TODO: NamH
)
