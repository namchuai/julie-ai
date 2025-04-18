package ai.julie.core.model.chatcompletion.create

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


// --- Serializable ResponseFormat Definition ---

@Serializable
data class JsonSchemaDetailsRequest(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("schema") val schema: JsonElement? = null, // Use JsonElement for JSON schema
    @SerialName("strict") val strict: Boolean? = null
)

@Serializable
sealed class ResponseFormatOptionRequest {
    abstract val type: String
}

@Serializable
@SerialName("text")
object TextResponseFormatRequest : ResponseFormatOptionRequest() {
    @Required
    @SerialName("type")
    override val type: String = "text"
}

@Serializable
@SerialName("json_object")
object JsonObjectResponseFormatRequest : ResponseFormatOptionRequest() {
    @Required
    @SerialName("type")
    override val type: String = "json_object"
}

@Serializable
@SerialName("json_schema")
data class JsonSchemaResponseFormatRequest(
    @SerialName("json_schema") val jsonSchema: JsonSchemaDetailsRequest
) : ResponseFormatOptionRequest() {
    @Required
    @SerialName("type")
    override val type: String = "json_schema"
} 