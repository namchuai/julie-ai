package ai.julie.core.network.model.request

import ai.julie.core.model.createchatcompletion.ApproximateLocation
import ai.julie.core.model.createchatcompletion.AssistantAudio
import ai.julie.core.model.createchatcompletion.AssistantMessage
import ai.julie.core.model.createchatcompletion.AudioContentPart
import ai.julie.core.model.createchatcompletion.AudioParameters
import ai.julie.core.model.createchatcompletion.ContentPart
import ai.julie.core.model.createchatcompletion.CreateChatCompletionRequestBody
import ai.julie.core.model.createchatcompletion.DeveloperMessage
import ai.julie.core.model.createchatcompletion.FileContentPart
import ai.julie.core.model.createchatcompletion.FileData
import ai.julie.core.model.createchatcompletion.FunctionCallOption
import ai.julie.core.model.createchatcompletion.FunctionDescription
import ai.julie.core.model.createchatcompletion.ImageContentPart
import ai.julie.core.model.createchatcompletion.ImageUrl
import ai.julie.core.model.createchatcompletion.InputAudio
import ai.julie.core.model.createchatcompletion.JsonObjectFormat
import ai.julie.core.model.createchatcompletion.JsonSchemaFormat
import ai.julie.core.model.createchatcompletion.Message
import ai.julie.core.model.createchatcompletion.MessageContent
import ai.julie.core.model.createchatcompletion.PredictionConfig
import ai.julie.core.model.createchatcompletion.PredictionContent
import ai.julie.core.model.createchatcompletion.PredictionContentPart
import ai.julie.core.model.createchatcompletion.RefusalContentPart
import ai.julie.core.model.createchatcompletion.ResponseFormatOption
import ai.julie.core.model.createchatcompletion.StopOption
import ai.julie.core.model.createchatcompletion.StreamOptions
import ai.julie.core.model.createchatcompletion.SystemMessage
import ai.julie.core.model.createchatcompletion.TextContentPart
import ai.julie.core.model.createchatcompletion.TextFormat
import ai.julie.core.model.createchatcompletion.Tool
import ai.julie.core.model.createchatcompletion.ToolChoiceOption
import ai.julie.core.model.createchatcompletion.ToolFunction
import ai.julie.core.model.createchatcompletion.UserLocation
import ai.julie.core.model.createchatcompletion.UserMessage
import ai.julie.core.model.createchatcompletion.WebSearchOptions
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class ImageUrlRequest(
    @SerialName("url") val url: String,
    @SerialName("detail") val detail: String? = null
)

@Serializable
data class InputAudioRequest(
    @SerialName("data") val data: String,
    @SerialName("format") val format: String
)

@Serializable
data class FileDataRequest(
    @SerialName("file_data") val fileData: String? = null,
    @SerialName("file_id") val fileId: String? = null,
    @SerialName("filename") val filename: String? = null
)

@Serializable
sealed class ContentPartRequest {
    abstract val type: String
}

@Serializable
@SerialName("text")
data class TextContentPartRequest(
    @SerialName("text") val text: String
) : ContentPartRequest() {
    @Required
    @SerialName("type")
    override val type: String = "text" // Explicit type for serialization
}

@Serializable
@SerialName("image_url")
data class ImageContentPartRequest(
    @SerialName("image_url") val imageUrl: ImageUrlRequest
) : ContentPartRequest() {
    @Required
    @SerialName("type")
    override val type: String = "image_url"
}

@Serializable
@SerialName("input_audio")
data class AudioContentPartRequest(
    @SerialName("input_audio") val inputAudio: InputAudioRequest
) : ContentPartRequest() {
    @Required
    @SerialName("type")
    override val type: String = "input_audio"
}

@Serializable
@SerialName("file")
data class FileContentPartRequest(
    @SerialName("file") val file: FileDataRequest
) : ContentPartRequest() {
    @Required
    @SerialName("type")
    override val type: String = "file"
}

@Serializable
@SerialName("refusal")
data class RefusalContentPartRequest(
    @SerialName("refusal") val refusal: String
) : ContentPartRequest() {
    @Required
    @SerialName("type")
    override val type: String = "refusal"
}

// --- Serializable Message Content Definition ---

@Serializable(with = MessageContentRequestSerializer::class) // Needs custom serializer for string OR list
sealed class MessageContentRequest

internal data class TextMessageContentRequest(val value: String) : MessageContentRequest()
internal data class PartsMessageContentRequest(val value: List<ContentPartRequest>) :
    MessageContentRequest()

// Custom Serializer for MessageContentRequest (String or List<ContentPartRequest>)
internal object MessageContentRequestSerializer : KSerializer<MessageContentRequest> {
    private val stringSerializer = String.serializer()
    private val listSerializer = ListSerializer(ContentPartRequest.serializer())

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("MessageContentRequest", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: MessageContentRequest) {
        when (value) {
            is TextMessageContentRequest -> encoder.encodeSerializableValue(
                stringSerializer,
                value.value
            )

            is PartsMessageContentRequest -> encoder.encodeSerializableValue(
                listSerializer,
                value.value
            )
        }
    }

    override fun deserialize(decoder: Decoder): MessageContentRequest {
        // This is primarily for request generation, deserialization might not be strictly needed here
        // but providing a basic attempt. A more robust implementation might inspect the JsonElement.
        throw NotImplementedError("Deserialization not implemented for MessageContentRequest")
    }
}


// --- Serializable Message Definitions ---

@Serializable
data class AssistantAudioRequest(
    @SerialName("id") val id: String
)

@Serializable
sealed class MessageRequest {
    abstract val role: String
    abstract val name: String?
}

@Serializable
@SerialName("developer")
data class DeveloperMessageRequest(
    @SerialName("content") val content: MessageContentRequest,
    @SerialName("name") override val name: String? = null
) : MessageRequest() {
    @Required
    @SerialName("role")
    override val role: String = "developer"
}

@Serializable
@SerialName("system")
data class SystemMessageRequest(
    @SerialName("content") val content: MessageContentRequest,
    @SerialName("name") override val name: String? = null
) : MessageRequest() {
    @Required
    @SerialName("role")
    override val role: String = "system"
}

@Serializable
@SerialName("user")
data class UserMessageRequest(
    @SerialName("content") val content: MessageContentRequest,
    @SerialName("name") override val name: String? = null
) : MessageRequest() {
    @Required
    @SerialName("role")
    override val role: String = "user"
}

@Serializable
@SerialName("assistant")
data class AssistantMessageRequest(
    @SerialName("audio") val audio: AssistantAudioRequest? = null,
    @SerialName("content") val content: MessageContentRequest? = null, // Content is optional for assistant
    @SerialName("name") override val name: String? = null
) : MessageRequest() {
    @Required
    @SerialName("role")
    override val role: String = "assistant"
}

// --- Serializable FunctionCallOption Definition ---
// Needs custom serializer to handle string ("none", "auto") or object {"name": "..."}
@Serializable(with = FunctionCallOptionRequestSerializer::class)
sealed class FunctionCallOptionRequest

internal object NoneFunctionCallOptionRequest : FunctionCallOptionRequest()
internal object AutoFunctionCallOptionRequest : FunctionCallOptionRequest()
internal data class SpecificFunctionCallOptionRequest(val name: String) :
    FunctionCallOptionRequest()

@Serializable // Helper structure for the object case
private data class FunctionCallNameObj(val name: String)

// Custom Serializer for FunctionCallOptionRequest
internal object FunctionCallOptionRequestSerializer : KSerializer<FunctionCallOptionRequest> {
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("FunctionCallOptionRequest", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: FunctionCallOptionRequest) {
        when (value) {
            is NoneFunctionCallOptionRequest -> encoder.encodeString("none")
            is AutoFunctionCallOptionRequest -> encoder.encodeString("auto")
            is SpecificFunctionCallOptionRequest -> encoder.encodeSerializableValue(
                FunctionCallNameObj.serializer(),
                FunctionCallNameObj(value.name)
            )
        }
    }

    override fun deserialize(decoder: Decoder): FunctionCallOptionRequest {
        throw NotImplementedError("Deserialization not implemented for FunctionCallOptionRequest")
    }
}

// --- Serializable FunctionDescription Definition ---
@Serializable
data class FunctionDescriptionRequest(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("parameters") val parameters: JsonElement? = null // Use JsonElement for JSON Schema obj
)

// --- Serializable AudioParameters Definition ---
@Serializable
data class AudioParametersRequest(
    @SerialName("format") val format: String,
    @SerialName("voice") val voice: String
)

// --- Serializable Prediction Definition ---
@Serializable
data class PredictionContentPartRequest(
    @SerialName("text") val text: String,
    @SerialName("type") val type: String
)

// Need custom serializer for string or list
@Serializable(with = PredictionContentRequestSerializer::class)
sealed class PredictionContentRequest
internal data class TextPredictionContentRequest(val value: String) : PredictionContentRequest()
internal data class PartsPredictionContentRequest(val value: List<PredictionContentPartRequest>) :
    PredictionContentRequest()

internal object PredictionContentRequestSerializer : KSerializer<PredictionContentRequest> {
    private val stringSerializer = String.serializer()
    private val listSerializer = ListSerializer(PredictionContentPartRequest.serializer())

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("PredictionContentRequest", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: PredictionContentRequest) {
        when (value) {
            is TextPredictionContentRequest -> stringSerializer.serialize(encoder, value.value)
            is PartsPredictionContentRequest -> listSerializer.serialize(encoder, value.value)
        }
    }

    override fun deserialize(decoder: Decoder): PredictionContentRequest {
        throw NotImplementedError("Deserialization not implemented for PredictionContentRequest")
    }
}


@Serializable
data class PredictionConfigRequest(
    @SerialName("content") val content: PredictionContentRequest,
    @SerialName("type") val type: String = "content"
)


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


// --- Serializable StreamOptions Definition ---
@Serializable
data class StreamOptionsRequest(
    @SerialName("include_usage") val includeUsage: Boolean? = null
)


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

// --- Serializable WebSearchOptions Definition ---
@Serializable
data class ApproximateLocationRequest(
    @SerialName("city") val city: String? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("region") val region: String? = null,
    @SerialName("timezone") val timezone: String? = null,
    @SerialName("type") val type: String = "approximate"
)

@Serializable
data class UserLocationRequest(
    @SerialName("approximate") val approximate: ApproximateLocationRequest
)

@Serializable
data class WebSearchOptionsRequest(
    @SerialName("search_context_size") val searchContextSize: String? = null,
    @SerialName("user_location") val userLocation: UserLocationRequest? = null
)


// --- Main Request Body ---
@Serializable
data class CreateChatCompletionRequest(
    @SerialName("messages") val messages: List<MessageRequest>,
    @SerialName("model") val model: String,
    @SerialName("audio") val audio: AudioParametersRequest? = null,
    @SerialName("frequency_penalty") val frequencyPenalty: Double? = null,
    @SerialName("function_call") @Deprecated("Use tool_choice") val functionCall: FunctionCallOptionRequest? = null, // Deprecated
    @SerialName("functions") @Deprecated("Use tools") val functions: List<FunctionDescriptionRequest>? = null, // Deprecated
    @SerialName("logit_bias") val logitBias: Map<String, Int>? = null,
    @SerialName("logprobs") val logprobs: Boolean? = null,
    @SerialName("max_completion_tokens") val maxCompletionTokens: Int? = null,
    @SerialName("max_tokens") @Deprecated("Use max_completion_tokens") val maxTokens: Int? = null, // Deprecated
    @SerialName("metadata") val metadata: Map<String, String>? = null,
    @SerialName("modalities") val modalities: List<String>? = null,
    @SerialName("n") val n: Int? = null,
    @SerialName("parallel_tool_calls") val parallelToolCalls: Boolean? = null,
    @SerialName("prediction") val prediction: PredictionConfigRequest? = null,
    @SerialName("presence_penalty") val presencePenalty: Double? = null,
    @SerialName("reasoning_effort") val reasoningEffort: String? = null,
    @SerialName("response_format") val responseFormat: ResponseFormatOptionRequest? = null,
    @SerialName("seed") val seed: Int? = null,
    @SerialName("service_tier") val serviceTier: String? = null,
    @SerialName("stop") val stop: JsonElement? = null, // Use JsonElement for String | List<String>
    @SerialName("store") val store: Boolean? = null,
    @SerialName("stream") val stream: Boolean? = null,
    @SerialName("stream_options") val streamOptions: StreamOptionsRequest? = null,
    @SerialName("temperature") val temperature: Double? = null,
    @SerialName("tool_choice") val toolChoice: ToolChoiceOptionRequest? = null,
    @SerialName("tools") val tools: List<ToolRequest>? = null,
    @SerialName("top_logprobs") val topLogprobs: Int? = null,
    @SerialName("top_p") val topP: Double? = null,
    @SerialName("user") val user: String? = null,
    @SerialName("web_search_options") val webSearchOptions: WebSearchOptionsRequest? = null
)

// --- Mapping Functions ---

// Domain -> Request Mappers

internal fun ImageUrl.toRequest() = ImageUrlRequest(url = url, detail = detail)
internal fun InputAudio.toRequest() = InputAudioRequest(data = data, format = format)
internal fun FileData.toRequest() =
    FileDataRequest(fileData = fileData, fileId = fileId, filename = filename)

internal fun ContentPart.toRequest(): ContentPartRequest = when (this) {
    is TextContentPart -> TextContentPartRequest(text = text)
    is ImageContentPart -> ImageContentPartRequest(imageUrl = imageUrl.toRequest())
    is AudioContentPart -> AudioContentPartRequest(inputAudio = inputAudio.toRequest())
    is FileContentPart -> FileContentPartRequest(file = file.toRequest())
    is RefusalContentPart -> RefusalContentPartRequest(refusal = refusal)
}

internal fun MessageContent.toRequest(): MessageContentRequest = when (this) {
    is MessageContent.Text -> TextMessageContentRequest(value = value)
    is MessageContent.Parts -> PartsMessageContentRequest(value = value.map { it.toRequest() })
}

internal fun AssistantAudio.toRequest() = AssistantAudioRequest(id = id)

internal fun Message.toRequest(): MessageRequest = when (this) {
    is DeveloperMessage -> DeveloperMessageRequest(content = content.toRequest(), name = name)
    is SystemMessage -> SystemMessageRequest(content = content.toRequest(), name = name)
    is UserMessage -> UserMessageRequest(
        content = TextMessageContentRequest(content),
        name = name
    ) // TODO: recheck
    is AssistantMessage -> AssistantMessageRequest(
        audio = audio?.toRequest(),
        content = content?.toRequest(),
        name = name
    )
}

internal fun FunctionCallOption.toRequest(): FunctionCallOptionRequest = when (this) {
    is FunctionCallOption.None -> NoneFunctionCallOptionRequest
    is FunctionCallOption.Auto -> AutoFunctionCallOptionRequest
    is FunctionCallOption.Specific -> SpecificFunctionCallOptionRequest(name = name)
}

// Helper to convert Domain Parameters (Any?) to JsonElement
// Uses kotlinx.serialization Json
internal fun Any?.toJsonElement(json: Json = Json): JsonElement {
    return when (this) {
        null -> JsonNull
        is JsonElement -> this // Already JsonElement
        is String -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        is Map<*, *> -> {
            // Basic Map -> JsonObject conversion attempt
            val mapSerializer =
                MapSerializer(String.serializer(), JsonElement.serializer()) // Assume string keys
            try {
                // We need to ensure values are also JsonElements or serializable
                // This basic conversion might fail for complex nested objects within the map
                val jsonMap = this.mapNotNull { (k, v) ->
                    (k as? String)?.let { key ->
                        key to v.toJsonElement(json)
                    }
                }.toMap()
                json.encodeToJsonElement(mapSerializer, jsonMap)
            } catch (e: Exception) {
                // Log warning or return JsonNull if conversion fails
                println("Warning: Could not convert Map to JsonElement: ${e.message}")
                JsonNull
            }
        }

        is Iterable<*> -> JsonArray(this.map { it.toJsonElement(json) })
        else -> {
            throw IllegalStateException("NamH can't parse")
            // Attempt generic serialization for unknown types - might fail
//            try {
//                // This requires the type to be @Serializable or have a registered serializer
//                json.encodeToJsonElement(serializer(this::class.javaObjectType), this)
//            } catch (e: Exception) {
//                println("Warning: Could not convert type ${this::class.simpleName} to JsonElement: ${e.message}")
//                JsonNull // Fallback for non-serializable or complex types
//            }
        }
    }
}


@Deprecated("Use tools")
internal fun FunctionDescription.toRequest(json: Json = Json) = FunctionDescriptionRequest(
    name = name,
    description = description,
    parameters = parameters.toJsonElement(json) // Map Any? to JsonElement?
)

internal fun AudioParameters.toRequest() = AudioParametersRequest(format = format, voice = voice)

internal fun PredictionContentPart.toRequest() =
    PredictionContentPartRequest(text = text, type = type)

internal fun PredictionContent.toRequest(): PredictionContentRequest = when (this) {
    is PredictionContent.Text -> TextPredictionContentRequest(value = value)
    is PredictionContent.Parts -> PartsPredictionContentRequest(value = value.map { it.toRequest() })
}

internal fun PredictionConfig.toRequest() =
    PredictionConfigRequest(content = content.toRequest(), type = type)

internal fun JsonSchemaFormat.JsonSchemaDetails.toRequest(json: Json = Json) =
    JsonSchemaDetailsRequest(
        name = name,
        description = description,
        schema = schema?.let { json.encodeToJsonElement(it) }, // Assuming schema is Map<String, Any> serializable
        strict = strict
    )

internal fun ResponseFormatOption.toRequest(json: Json = Json): ResponseFormatOptionRequest =
    when (this) {
        is TextFormat -> TextResponseFormatRequest
        is JsonObjectFormat -> JsonObjectResponseFormatRequest
        is JsonSchemaFormat -> JsonSchemaResponseFormatRequest(
            jsonSchema = jsonSchema.toRequest(
                json
            )
        )
    }

internal fun StopOption.toJsonElement(): JsonElement = when (this) {
    is StopOption.Single -> JsonPrimitive(value)
    is StopOption.Multiple -> JsonArray(values.map { JsonPrimitive(it) })
}

internal fun StreamOptions.toRequest() = StreamOptionsRequest(includeUsage = includeUsage)

internal fun ToolChoiceOption.FunctionReference.toRequest() = FunctionReferenceRequest(name = name)
internal fun ToolChoiceOption.toRequest(): ToolChoiceOptionRequest = when (this) {
    is ToolChoiceOption.None -> NoneToolChoiceOptionRequest
    is ToolChoiceOption.Auto -> AutoToolChoiceOptionRequest
    is ToolChoiceOption.Required -> RequiredToolChoiceOptionRequest
    is ToolChoiceOption.SpecificTool -> SpecificToolChoiceOptionRequest(
        type = type,
        function = function.toRequest()
    )
}

internal fun ToolFunction.toRequest(json: Json = Json) = ToolFunctionRequest(
    name = name,
    description = description,
    parameters = parameters?.let { json.encodeToJsonElement(it) }, // Assuming Map<String, Any>
    strict = strict
)

internal fun Tool.toRequest(json: Json = Json) =
    ToolRequest(type = type, function = function.toRequest(json))

internal fun ApproximateLocation.toRequest() = ApproximateLocationRequest(
    city = city,
    country = country,
    region = region,
    timezone = timezone,
    type = type
)

internal fun UserLocation.toRequest() = UserLocationRequest(approximate = approximate.toRequest())
internal fun WebSearchOptions.toRequest() = WebSearchOptionsRequest(
    searchContextSize = searchContextSize,
    userLocation = userLocation?.toRequest()
)


/**
 * Converts the domain model `CreateChatCompletionRequestBody` to the network-serializable `CreateChatCompletionRequest`.
 */
fun CreateChatCompletionRequestBody.toCreateChatCompletionRequest(json: Json = Json): CreateChatCompletionRequest {
    return CreateChatCompletionRequest(
        messages = messages.map { it.toRequest() },
        model = model,
        audio = audio?.toRequest(),
        frequencyPenalty = frequencyPenalty,
        functionCall = functionCall?.toRequest(), // Deprecated
        functions = functions?.map { it.toRequest(json) }, // Deprecated
        logitBias = logitBias,
        logprobs = logprobs,
        maxCompletionTokens = maxCompletionTokens,
        maxTokens = maxTokens, // Deprecated
        metadata = metadata,
        modalities = modalities,
        n = n,
//        parallelToolCalls = parallelToolCalls, // TODO: can only be used with tools
        prediction = prediction?.toRequest(),
        presencePenalty = presencePenalty,
//        reasoningEffort = reasoningEffort,
        responseFormat = responseFormat?.toRequest(json),
        seed = seed,
        serviceTier = serviceTier,
        stop = stop?.toJsonElement(), // Map StopOption to JsonElement
        store = store,
        stream = stream,
        streamOptions = streamOptions?.toRequest(),
        temperature = temperature,
        toolChoice = toolChoice?.toRequest(),
        tools = tools?.map { it.toRequest(json) },
        topLogprobs = topLogprobs,
        topP = topP,
        user = user,
        webSearchOptions = webSearchOptions?.toRequest()
    )
}

