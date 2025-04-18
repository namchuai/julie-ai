package ai.julie.core.model.chatcompletion.create

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

/**
 * Represents the "stop" parameter which can be a string, array of strings, or null.
 *
 * Up to 4 sequences where the API will stop generating further tokens.
 * The returned text will not contain the stop sequence.
 *
 * Not supported with latest reasoning models o3 and o4-mini.
 */
@Serializable(with = StopParameterSerializer::class)
sealed class StopParameter {
    /**
     * No stop sequences defined (default).
     */
    @Serializable
    data object None : StopParameter()

    /**
     * A single stop sequence.
     *
     * @property value The stop sequence string
     */
    @Serializable
    data class Single(val value: String) : StopParameter()

    /**
     * Multiple stop sequences (up to 4).
     *
     * @property values List of stop sequence strings
     */
    @Serializable
    data class Multiple(val values: List<String>) : StopParameter() {
        init {
            require(values.size <= 4) { "Maximum of 4 stop sequences are allowed" }
        }
    }

    companion object {
        /**
         * Creates a StopParameter from vararg strings.
         *
         * @param sequences Stop sequence strings
         * @return Appropriate StopParameter type based on input
         */
        fun from(vararg sequences: String): StopParameter = when {
            sequences.isEmpty() -> None
            sequences.size == 1 -> Single(sequences[0])
            sequences.size <= 4 -> Multiple(sequences.toList())
            else -> throw IllegalArgumentException("Maximum of 4 stop sequences are allowed")
        }
    }
}

/**
 * Custom serializer for StopParameter that handles null, string, and array formats.
 */
object StopParameterSerializer : KSerializer<StopParameter> {
    // Using JsonElement as the intermediate representation
    private val jsonElementSerializer = JsonElement.serializer()

    override val descriptor: SerialDescriptor =
        SerialDescriptor("StopParameter", JsonElement.serializer().descriptor)

    override fun serialize(encoder: Encoder, value: StopParameter) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("This serializer can only be used with JSON")

        val jsonElement = when (value) {
            is StopParameter.None -> JsonNull
            is StopParameter.Single -> JsonPrimitive(value.value)
            is StopParameter.Multiple -> JsonArray(value.values.map { JsonPrimitive(it) })
        }

        jsonEncoder.encodeJsonElement(jsonElement)
    }

    override fun deserialize(decoder: Decoder): StopParameter {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("This serializer can only be used with JSON")

        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonNull -> StopParameter.None
            is JsonPrimitive -> StopParameter.Single(element.content)
            is JsonArray -> {
                val strings = element.map {
                    (it as? JsonPrimitive)?.content
                        ?: throw SerializationException("Stop sequences must be strings")
                }

                require(strings.size <= 4) { "Maximum of 4 stop sequences are allowed" }

                if (strings.isEmpty()) {
                    StopParameter.None
                } else if (strings.size == 1) {
                    StopParameter.Single(strings[0])
                } else {
                    StopParameter.Multiple(strings)
                }
            }

            else -> throw SerializationException("Unexpected JSON element type for stop parameter")
        }
    }
}
