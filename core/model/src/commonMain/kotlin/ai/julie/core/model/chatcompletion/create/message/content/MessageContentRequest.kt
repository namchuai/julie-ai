package ai.julie.core.model.chatcompletion.create.message.content

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.jvm.JvmInline

@Serializable(with = MessageContentRequestSerializer::class)
sealed interface MessageContentRequest {
    @Serializable
    @JvmInline
    value class Value(val value: String) : MessageContentRequest

    @Serializable
    @JvmInline
    value class PartsValue(val value: List<ContentPart>) : MessageContentRequest
}

object MessageContentRequestSerializer :
    JsonContentPolymorphicSerializer<MessageContentRequest>(MessageContentRequest::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<MessageContentRequest> =
        when (element) {
            is JsonPrimitive -> MessageContentRequest.Value.serializer()
            is JsonArray -> MessageContentRequest.PartsValue.serializer()
            else -> throw SerializationException(
                "Unexpected JSON element type for MessageContentRequest: ${element::class.simpleName}"
            )
        }
} 