package ai.julie.core.model.chatcompletion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Logprobs(

    /**
     * A list of message content tokens with log probability information.
     */
    @SerialName("content") val content: List<LogprobsContent>? = null,

    /**
     * A list of message refusal tokens with log probability information.
     */
    @SerialName("refusal") val refusal: List<LogprobsRefusal>? = null,
)

/**
 * A list of message content tokens with log probability information.
 */
@Serializable
data class LogprobsContent(
    /**
     * The log probability of this token, if it is within the top 20 most likely tokens.
     * Otherwise, the value -9999.0 is used to signify that the token is very unlikely.
     */
    val logprob: Double,

    /**
     * The token.
     */
    val token: String,

    // TODO:
    // top_logprobs
    // bytes
)

@Serializable
data class LogprobsRefusal(
    /**
     * The log probability of this token, if it is within the top 20 most likely tokens.
     * Otherwise, the value -9999.0 is used to signify that the token is very unlikely.
     */
    val logprob: Double,

    /**
     * The token.
     */
    val token: String,

    // TODO:
    // top_logprobs
    // bytes
)
