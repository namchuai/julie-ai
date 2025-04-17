package ai.julie.core.network.model.createchatcompletion.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ai.julie.core.model.chatcompletionresponse.ChatCompletion as ModelChatCompletion

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

fun ChatCompletion.toChatCompletion() = ModelChatCompletion(
    id = this.id,
    choices = this.choices.map { it.toChoice() },
    created = this.created,
    model = this.model,
    objectType = this.objectType,
    serviceTier = this.serviceTier,
    systemFingerprint = this.systemFingerprint,
    usage = this.usage?.toUsage(),
)
