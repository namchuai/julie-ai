package ai.julie.core.model.createchatcompletion

/**
 * Represents the different structures for the `response_format` field,
 * specifying the format the model must output.
 */
sealed class ResponseFormatOption(open val type: String)

/**
 * Represents the default text response format (`{"type": "text"}`).
 */
object TextFormat : ResponseFormatOption("text")

/**
 * Represents the older JSON object response format (`{"type": "json_object"}`).
 * Using `JsonSchemaFormat` is generally preferred.
 */
object JsonObjectFormat : ResponseFormatOption("json_object")

/**
 * Represents the JSON Schema response format (`{"type": "json_schema", "json_schema": {...}}`).
 * Used to generate structured JSON responses based on a provided schema.
 */
data class JsonSchemaFormat(
    /**
     * Required.
     * Contains the configuration options for the JSON Schema, including the schema itself.
     */
    val jsonSchema: JsonSchemaDetails
) : ResponseFormatOption("json_schema") {

    /**
     * Details for the JSON Schema response format configuration.
     */
    data class JsonSchemaDetails(
        /**
         * Required.
         * The name of the response format. Must be a-z, A-Z, 0-9, or contain underscores and dashes, with a maximum length of 64.
         */
        val name: String,

        /**
         * Optional.
         * A description of what the response format is for, used by the model.
         */
        val description: String? = null,

        /**
         * Optional.
         * The schema for the response format, described as a JSON Schema object.
         */
        val schema: Map<String, Any>? = null, // Using Map<String, Any> for JSON object representation

        /**
         * Optional.
         * Defaults to false.
         * Whether to enable strict schema adherence. If true, the model will strictly follow the schema.
         * Only a subset of JSON Schema is supported when strict is true.
         */
        val strict: Boolean? = false
    )

    // Override type from the sealed class constructor - this field is implicitly part of the structure
    override val type: String
        get() = "json_schema"
} 