package ai.julie.feature.modelconfig.domain

import ai.julie.feature.modelconfig.domain.preset.SamplingPreset
import kotlinx.coroutines.flow.Flow

fun interface FlowOfSamplingPresets {
    fun flowOfSamplingPresets(): Flow<List<SamplingPreset>>
}
