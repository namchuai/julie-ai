package ai.julie.core.data.llama

import ai.julie.core.model.LlamaSamplerSettings
import kotlinx.coroutines.flow.Flow

fun interface Promptable {
    fun prompt(input: String, samplerSettings: LlamaSamplerSettings): Flow<String>
}