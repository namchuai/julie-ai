package ai.julie.core.model.chatcompletion.create

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AudioFormat {
    @SerialName("wav") WAV,
    @SerialName("mp3") MP3,
    @SerialName("flac") FLAC,
    @SerialName("opus") OPUS,
    @SerialName("pcm16") PCM16
}

@Serializable
enum class AudioVoice {
    @SerialName("alloy") ALLOY,
    @SerialName("ash") ASH,
    @SerialName("ballad") BALLAD,
    @SerialName("coral") CORAL,
    @SerialName("echo") ECHO,
    @SerialName("fable") FABLE,
    @SerialName("nova") NOVA,
    @SerialName("onyx") ONYX,
    @SerialName("sage") SAGE,
    @SerialName("shimmer") SHIMMER
}

@Serializable
data class Audio(
    /**
     * Specifies the output audio format. Must be one of wav, mp3, flac, opus, or pcm16.
     */
    @SerialName("format") val format: AudioFormat,

    /**
     * The voice the model uses to respond. Supported voices are alloy, ash, ballad, coral, echo,
     * fable, nova, onyx, sage, and shimmer.
     */
    @SerialName("voice") val voice: AudioVoice
)
