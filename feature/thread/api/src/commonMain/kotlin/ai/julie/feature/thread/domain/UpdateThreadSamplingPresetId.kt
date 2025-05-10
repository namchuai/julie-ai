package ai.julie.feature.thread.domain

fun interface UpdateThreadSamplingPresetId {
    suspend fun updateThreadSamplingPresetId(threadId: String, samplingPresetId: String)
}