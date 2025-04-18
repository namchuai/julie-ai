package ai.julie.core.model.chatcompletion.create

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = FunctionCallOptionRequestSerializer::class)
sealed class FunctionCall

internal data object None : FunctionCall()
internal data object Auto : FunctionCall()
internal data class Specific(val name: String) :
    FunctionCall()

@Serializable // Helper structure for the object case
private data class FunctionCallNameObj(val name: String)

// Custom Serializer for FunctionCallOptionRequest
internal object FunctionCallOptionRequestSerializer : KSerializer<FunctionCall> {
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("FunctionCallOptionRequest", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: FunctionCall) {
        when (value) {
            is None -> encoder.encodeString("none")
            is Auto -> encoder.encodeString("auto")
            is Specific -> encoder.encodeSerializableValue(
                FunctionCallNameObj.serializer(),
                FunctionCallNameObj(value.name)
            )
        }
    }

    override fun deserialize(decoder: Decoder): FunctionCall {
        // Deserialization might be needed if you ever parse responses with this field
        throw NotImplementedError("Deserialization not implemented for FunctionCallOptionRequest")
    }
}
