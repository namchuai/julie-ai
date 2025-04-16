package ai.julie.core.model.createchatcompletion

/**
 * Represents a part of the content for a predicted output, used when content is an array.
 */
data class PredictionContentPart(
    /**
     * Required.
     * The text content.
     */
    val text: String,

    /**
     * Required.
     * The type of the content part.
     */
    val type: String
)

/**
 * Represents the content for a predicted output. It can be simple text or an array of parts.
 */
sealed class PredictionContent {
    /** Represents simple text content. */
    data class Text(val value: String) : PredictionContent()

    /** Represents content as an array of parts. */
    data class Parts(val value: List<PredictionContentPart>) : PredictionContent()
}

/**
 * Configuration for a Predicted Output.
 * This can greatly improve response times when large parts of the model response are known ahead of time.
 */
data class PredictionConfig(
    /**
     * Required.
     * The content that should be matched when generating a model response.
     * Can be simple text or an array of content parts.
     */
    val content: PredictionContent,

    /**
     * Required.
     * The type of the predicted content you want to provide. This type is currently always "content".
     */
    val type: String = "content" // Defaulting as per description
) 