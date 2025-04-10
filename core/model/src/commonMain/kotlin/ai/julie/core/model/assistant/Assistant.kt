package ai.julie.core.model.assistant

/**
 * Represents an `assistant` that can call the model and use tools.
 */
data class Assistant(
    /**
     * The identifier, which can be referenced in API endpoints.
     */
    val id: String,

    /**
     * ID of the model to use.
     */
    val model: String,

    /**
     * The Unix timestamp (in seconds) for when the assistant was created.
     */
    val createdAt: Long,

    /**
     * The name of the assistant. The maximum length is 256 characters.
     */
    val name: String? = null,

    /**
     * The description of the assistant. The maximum length is 512 characters.
     */
    val description: String? = null,

    /**
     * The system instructions that the assistant uses. The maximum length is 256,000 characters.
     */
    val instructions: String? = null,

    /**
     * A list of tool enabled on the assistant. There can be a maximum of 128 tools per assistant.
     */
    val tools: List<AssistantTool> = emptyList(),

    /**
     * A set of resources that are used by the assistant's tools.
     */
    val toolResources: ToolResources? = null,

    /**
     * Set of key-value pairs that can be attached to an object for additional information.
     */
    val metadata: Map<String, String>? = null,

    /**
     * What sampling temperature to use, between 0 and 2.
     * Higher values like 0.8 will make the output more random,
     * while lower values like 0.2 will make it more focused and deterministic.
     */
    val temperature: Double? = null,

    /**
     * An alternative to sampling with temperature, called nucleus sampling,
     * where the model considers the results of the tokens with top_p probability mass.
     */
    val topP: Double? = null,

    /**
     * Specifies the format that the model must output.
     */
    val responseFormat: ResponseFormat = ResponseFormat.Auto
)