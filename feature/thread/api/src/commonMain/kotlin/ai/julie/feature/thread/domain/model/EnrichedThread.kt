package ai.julie.feature.thread.domain.model

import com.aallam.openai.api.thread.Thread

data class EnrichedThread(
    val thread: Thread,
    val modelId: String,
    val samplingPresetId: String? = null,
    val updatedAt: Int = thread.createdAt, // Default to createdAt for backward compatibility
) {
    // Delegate Thread properties for seamless access
    val id get() = thread.id.id
    val createdAt get() = thread.createdAt
    val metadata get() = thread.metadata
    val toolResources get() = thread.toolResources
}
