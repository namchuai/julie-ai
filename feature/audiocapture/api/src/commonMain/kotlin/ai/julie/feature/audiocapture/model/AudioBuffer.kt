package ai.julie.feature.audiocapture.model

import kotlinx.datetime.Instant

data class AudioBuffer(
    val data: ByteArray,
    val frameCount: Int,
    val timestamp: Instant,
    val format: AudioFormat
) {
    val sizeInBytes: Int = data.size
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AudioBuffer

        if (!data.contentEquals(other.data)) return false
        if (frameCount != other.frameCount) return false
        if (timestamp != other.timestamp) return false
        if (format != other.format) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + frameCount
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + format.hashCode()
        return result
    }
}