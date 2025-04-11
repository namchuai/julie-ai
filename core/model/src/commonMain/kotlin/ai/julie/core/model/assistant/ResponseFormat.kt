package ai.julie.core.model.assistant

/**
 * Specifies the format that the model must output.
 */
sealed class ResponseFormat {
    /**
     * Auto format for model responses (default value).
     * Used to generate text responses.
     */
    object Auto : ResponseFormat() {
        private const val type: String = "auto"

        override fun toString(): String = type
    }

    /**
     * Text format for model responses.
     * Default response format. Used to generate text responses.
     */
    data class Text(
        /**
         * The type of response format.
         */
        val type: String = "text"
    ) : ResponseFormat()

    /**
     * JSON object format for model responses.
     * An older method of generating JSON responses.
     * Note that the model will not generate JSON without a system or user message instructing it to do so.
     */
    data class JsonObject(
        /**
         * The type of response format.
         */
        val type: String = "json_object"
    ) : ResponseFormat()

    /**
     * JSON schema format for model responses (Structured Outputs).
     * Used to generate structured JSON responses.
     */
    data class JsonSchema(
        /**
         * The type of response format.
         */
        val type: String = "json_schema",

        /**
         * Structured Outputs configuration options, including a JSON Schema.
         */
        val jsonSchema: Map<String, Any>
    ) : ResponseFormat()
}
