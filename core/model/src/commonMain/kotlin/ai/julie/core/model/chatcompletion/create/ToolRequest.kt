package ai.julie.core.model.chatcompletion.create

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement

// --- Serializable ToolChoice Definition ---
// Needs custom serializer for string ("none", "auto", "required") or object
@Serializable(with = ToolChoiceOptionRequestSerializer::class)
sealed class ToolChoiceOptionRequest

internal object NoneToolChoiceOptionRequest : ToolChoiceOptionRequest()
internal object AutoToolChoiceOptionRequest : ToolChoiceOptionRequest()
internal object RequiredToolChoiceOptionRequest : ToolChoiceOptionRequest()
internal data class SpecificToolChoiceOptionRequest(
    val type: String = "function",
    val function: FunctionReferenceRequest
) : ToolChoiceOptionRequest()

@Serializable // Helper for SpecificToolChoiceOptionRequest
internal data class FunctionReferenceRequest(val name: String)

@Serializable // Structure for the object case in serialization
private data class SpecificToolChoiceObj(
    val type: String,
    val function: FunctionReferenceRequest
)

// Custom Serializer for ToolChoiceOptionRequest
internal object ToolChoiceOptionRequestSerializer : KSerializer<ToolChoiceOptionRequest> {
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("ToolChoiceOptionRequest", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: ToolChoiceOptionRequest) {
        when (value) {
            is NoneToolChoiceOptionRequest -> encoder.encodeString("none")
            is AutoToolChoiceOptionRequest -> encoder.encodeString("auto")
            is RequiredToolChoiceOptionRequest -> encoder.encodeString("required")
            is SpecificToolChoiceOptionRequest -> encoder.encodeSerializableValue(
                SpecificToolChoiceObj.serializer(),
                SpecificToolChoiceObj(value.type, value.function)
            )
        }
    }

    override fun deserialize(decoder: Decoder): ToolChoiceOptionRequest {
        throw NotImplementedError("Deserialization not implemented for ToolChoiceOptionRequest")
    }
}

// --- Serializable Tool Definition ---
@Serializable
data class ToolFunctionRequest(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("parameters") val parameters: JsonElement? = null, // JSON Schema object
    @SerialName("strict") val strict: Boolean? = null
)

@Serializable
data class ToolRequest(
    @SerialName("type") val type: String = "function",
    @SerialName("function") val function: ToolFunctionRequest
) 