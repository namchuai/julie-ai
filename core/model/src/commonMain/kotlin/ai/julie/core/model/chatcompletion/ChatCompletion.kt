package ai.julie.core.model.chatcompletion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletion(

    /**
     * A unique identifier for the chat completion.
     */
    @SerialName("id") val id: String,

    /**
     * A list of chat completion choices. Can be more than one if n is greater than 1.
     */
    @SerialName("choices") val choices: List<Choice>,

    /**
     * The Unix timestamp (in seconds) of when the chat completion was created.
     */
    @SerialName("created") val created: Int,

    /**
     * The model used for the chat completion.
     */
    @SerialName("model") val model: String,

    /**
     * The object type, which is always chat.completion.
     */
    @SerialName("object") val objectType: String,

    /**
     * The service tier used for processing the request.
     */
    @SerialName("service_tier") val serviceTier: String? = null,

    /**
     * This fingerprint represents the backend configuration that the model runs with.
     *
     * Can be used in conjunction with the seed request parameter to understand when backend changes
     * have been made that might impact determinism.
     */
    @SerialName("system_fingerprint") val systemFingerprint: String,

    /**
     * Usage statistics for the completion request.
     */
    @SerialName("usage") val usage: Usage,
)
