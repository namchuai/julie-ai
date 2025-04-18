package ai.julie.core.model.chatcompletion.create

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Function(
    /**
     * The name of the function to be called. Must be a-z, A-Z, 0-9, or contain underscores and
     * dashes, with a maximum length of 64.
     */
    @SerialName("name") val name: String,

    /**
     * A description of what the function does, used by the model to choose when and how to call the
     * function.
     */
    @SerialName("description") val description: String? = null,

    /**
     * The parameters the functions accepts, described as a JSON Schema object. See the
     * [guide](https://platform.openai.com/docs/guides/function-calling) for examples, and the
     * [JSON Schema reference](https://json-schema.org/understanding-json-schema/) for documentation
     * about the format.
     *
     * Omitting parameters defines a function with an empty parameter list.
     */
    @SerialName("parameters") val parameters: JsonElement? = null, // TODO: NamH
)