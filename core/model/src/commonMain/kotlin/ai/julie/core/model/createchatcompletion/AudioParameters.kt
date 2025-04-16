package ai.julie.core.model.createchatcompletion

/**
 * Parameters for audio output. Required when audio output is requested with modalities: ["audio"].
 */
data class AudioParameters(
    /**
     * Required.
     * Specifies the output audio format. Must be one of wav, mp3, flac, opus, or pcm16.
     */
    val format: String,

    /**
     * Required.
     * The voice the model uses to respond. Supported voices are alloy, ash, ballad, coral, echo, sage, and shimmer.
     */
    val voice: String
) 