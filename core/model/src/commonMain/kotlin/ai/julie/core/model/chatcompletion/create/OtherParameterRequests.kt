package ai.julie.core.model.chatcompletion.create

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// --- Serializable Prediction Definition ---
@Serializable
data class PredictionContentPartRequest(
    @SerialName("text") val text: String,
    @SerialName("type") val type: String
)

// Need custom serializer for string or list
@Serializable(with = PredictionContentRequestSerializer::class)
sealed class PredictionContentRequest
internal data class TextPredictionContentRequest(val value: String) : PredictionContentRequest()
internal data class PartsPredictionContentRequest(val value: List<PredictionContentPartRequest>) :
    PredictionContentRequest()

internal object PredictionContentRequestSerializer : KSerializer<PredictionContentRequest> {
    private val stringSerializer = String.serializer()
    private val listSerializer = ListSerializer(PredictionContentPartRequest.serializer())

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("PredictionContentRequest", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: PredictionContentRequest) {
        when (value) {
            is TextPredictionContentRequest -> stringSerializer.serialize(encoder, value.value)
            is PartsPredictionContentRequest -> listSerializer.serialize(encoder, value.value)
        }
    }

    override fun deserialize(decoder: Decoder): PredictionContentRequest {
        throw NotImplementedError("Deserialization not implemented for PredictionContentRequest")
    }
}

// --- Serializable WebSearchOptions Definition ---
@Serializable
data class ApproximateLocationRequest(
    @SerialName("city") val city: String? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("region") val region: String? = null,
    @SerialName("timezone") val timezone: String? = null,
    @SerialName("type") val type: String = "approximate"
)

@Serializable
data class UserLocationRequest(
    @SerialName("approximate") val approximate: ApproximateLocationRequest
)

@Serializable
data class WebSearchOptionsRequest(
    @SerialName("search_context_size") val searchContextSize: String? = null,
    @SerialName("user_location") val userLocation: UserLocationRequest? = null
)
