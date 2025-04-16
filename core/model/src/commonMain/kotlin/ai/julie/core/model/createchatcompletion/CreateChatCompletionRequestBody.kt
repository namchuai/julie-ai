package ai.julie.core.model.createchatcompletion

/**
 * Represents the request body for creating a chat completion.
 */
data class CreateChatCompletionRequestBody(
    /**
     * Required.
     * A list of messages comprising the conversation so far. Depending on the model you use,
     * different message types (modalities) are supported, like text, images, and audio.
     */
    val messages: List<Message>,

    /**
     * Required.
     * Model ID used to generate the response, like gpt-4o or o1.
     * OpenAI offers a wide range of models with different capabilities, performance characteristics, and price points.
     * Refer to the model guide to browse and compare available models.
     */
    val model: String,

    /**
     * Optional.
     * Parameters for audio output. Required when audio output is requested with modalities: ["audio"].
     */
    val audio: AudioParameters? = null,

    /**
     * Optional.
     * Defaults to 0.
     * Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency
     * in the text so far, decreasing the model's likelihood to repeat the same line verbatim.
     */
    val frequencyPenalty: Double? = 0.0,

    /**
     * Optional.
     * Deprecated in favor of tool_choice.
     * Controls which (if any) function is called by the model.
     * - `FunctionCallOption.None`: The model will not call a function and instead generates a message.
     * - `FunctionCallOption.Auto`: The model can pick between generating a message or calling a function.
     * - `FunctionCallOption.Specific`: Forces the model to call the specified function.
     * `None` is the default when no functions are present. `Auto` is the default if functions are present.
     */
    @Deprecated("Deprecated in favor of tool_choice.")
    val functionCall: FunctionCallOption? = null,

    /**
     * Optional.
     * Deprecated in favor of tools.
     * A list of functions the model may generate JSON inputs for.
     */
    @Deprecated("Deprecated in favor of tools.")
    val functions: List<FunctionDescription>? = null,

    /**
     * Optional.
     * Defaults to null.
     * Modify the likelihood of specified tokens appearing in the completion.
     * Accepts a JSON object that maps tokens (specified by their token ID in the tokenizer)
     * to an associated bias value from -100 to 100.
     */
    val logitBias: Map<String, Int>? = null,

    /**
     * Optional.
     * Defaults to false.
     * Whether to return log probabilities of the output tokens or not. If true,
     * returns the log probabilities of each output token returned in the content of message.
     */
    val logprobs: Boolean? = false,

    /**
     * Optional.
     * An upper bound for the number of tokens that can be generated for a completion,
     * including visible output tokens and reasoning tokens.
     */
    val maxCompletionTokens: Int? = null,

    /**
     * Optional.
     * Deprecated in favor of max_completion_tokens.
     * The maximum number of tokens that can be generated in the chat completion.
     * This value can be used to control costs for text generated via API.
     * This value is not compatible with o1 series models.
     */
    @Deprecated("Deprecated in favor of max_completion_tokens.")
    val maxTokens: Int? = null,

    /**
     * Optional.
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys are strings with a maximum length of 64 characters. Values are strings with a maximum length of 512 characters.
     */
    val metadata: Map<String, String>? = null,

    /**
     * Optional.
     * Output types that you would like the model to generate. Most models default to ["text"].
     * Example: ["text", "audio"]
     */
    val modalities: List<String>? = null,

    /**
     * Optional.
     * Defaults to 1.
     * How many chat completion choices to generate for each input message.
     * Note that you will be charged based on the number of generated tokens across all choices.
     */
    val n: Int? = 1,

    /**
     * Optional.
     * Defaults to true.
     * Whether to enable parallel function calling during tool use.
     */
    val parallelToolCalls: Boolean? = true,

    /**
     * Optional.
     * Configuration for a Predicted Output, which can greatly improve response times
     * when large parts of the model response are known ahead of time.
     */
    val prediction: PredictionConfig? = null,

    /**
     * Optional.
     * Defaults to 0.
     * Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear
     * in the text so far, increasing the model's likelihood to talk about new topics.
     */
    val presencePenalty: Double? = 0.0,

    /**
     * Optional.
     * Defaults to medium.
     * o-series models only.
     * Constrains effort on reasoning for reasoning models. Supported values: low, medium, high.
     */
    val reasoningEffort: String? = "medium",

    /**
     * Optional.
     * An object specifying the format that the model must output.
     * Can be `TextFormat`, `JsonObjectFormat`, or `JsonSchemaFormat`.
     * Setting to `JsonSchemaFormat` enables Structured Outputs.
     * Setting to `JsonObjectFormat` enables the older JSON mode.
     */
    val responseFormat: ResponseFormatOption? = null,

    /**
     * Optional.
     * This feature is in Beta. If specified, our system will make a best effort to sample deterministically.
     * Determinism is not guaranteed.
     */
    val seed: Int? = null,

    /**
     * Optional.
     * Defaults to auto.
     * Specifies the latency tier to use for processing the request (relevant for scale tier customers).
     * Values: 'auto', 'default'.
     */
    val serviceTier: String? = "auto",

    /**
     * Optional.
     * Defaults to null.
     * Sequences where the API will stop generating further tokens.
     * Can be a single sequence (`StopOption.Single`) or up to 4 sequences (`StopOption.Multiple`).
     * The returned text will not contain the stop sequence(s).
     */
    val stop: StopOption? = null,

    /**
     * Optional.
     * Defaults to false.
     * Whether or not to store the output of this chat completion request for use in model distillation or evals.
     */
    val store: Boolean? = false,

    /**
     * Optional.
     * Defaults to false.
     * If set to true, the model response data will be streamed to the client as it is generated using server-sent events.
     */
    val stream: Boolean? = false,

    /**
     * Optional.
     * Defaults to null.
     * Options for streaming response. Only set this when stream is true.
     */
    val streamOptions: StreamOptions? = null,

    /**
     * Optional.
     * Defaults to 1.
     * What sampling temperature to use, between 0 and 2. Higher values (e.g., 0.8) make the output more random,
     * lower values (e.g., 0.2) make it more focused and deterministic.
     * It's recommended to alter this or top_p, but not both.
     */
    val temperature: Double? = 1.0,

    /**
     * Optional.
     * Controls which (if any) tool is called by the model.
     * - `ToolChoiceOption.None`: The model will not call any tool.
     * - `ToolChoiceOption.Auto`: The model can pick between generating a message or calling one or more tools.
     * - `ToolChoiceOption.Required`: The model must call one or more tools.
     * - `ToolChoiceOption.SpecificTool`: Forces the model to call the specified function.
     * `None` is the default when no tools are present. `Auto` is the default if tools are present.
     */
    val toolChoice: ToolChoiceOption? = null,

    /**
     * Optional.
     * A list of tools the model may call. Currently, only functions are supported as a tool.
     * A max of 128 functions are supported.
     */
    val tools: List<Tool>? = null,

    /**
     * Optional.
     * An integer between 0 and 20 specifying the number of most likely tokens to return at each token position,
     * each with an associated log probability. `logprobs` must be set to true if this parameter is used.
     */
    val topLogprobs: Int? = null,

    /**
     * Optional.
     * Defaults to 1.
     * An alternative to sampling with temperature, called nucleus sampling.
     * The model considers the results of the tokens with top_p probability mass (e.g., 0.1 means top 10%).
     * It's recommended to alter this or temperature, but not both.
     */
    val topP: Double? = 1.0,

    /**
     * Optional.
     * A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     */
    val user: String? = null,

    /**
     * Optional.
     * Configuration for the web search tool.
     */
    val webSearchOptions: WebSearchOptions? = null
) 