package ai.julie.feature.audiocapture.model

data class AudioFormat(
    val sampleRate: Int,
    val channelCount: Int,
    val bitsPerChannel: Int
) {
    companion object {
        val DEFAULT = AudioFormat(
            sampleRate = 48000,
            channelCount = 2,
            bitsPerChannel = 16
        )
    }
}