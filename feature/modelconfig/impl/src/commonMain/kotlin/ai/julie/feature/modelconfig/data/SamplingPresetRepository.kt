package ai.julie.feature.modelconfig.data

import ai.julie.feature.modelconfig.domain.FlowOfSamplingPresets
import ai.julie.feature.modelconfig.domain.preset.SamplingPreset
import ai.julie.feature.modelconfig.domain.samplingparameter.MinP
import ai.julie.feature.modelconfig.domain.samplingparameter.Mirostat
import ai.julie.feature.modelconfig.domain.samplingparameter.RepeatPenalty
import ai.julie.feature.modelconfig.domain.samplingparameter.Temperature
import ai.julie.feature.modelconfig.domain.samplingparameter.TopK
import ai.julie.feature.modelconfig.domain.samplingparameter.TopP
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SamplingPresetRepository : FlowOfSamplingPresets {

    override fun flowOfSamplingPresets(): Flow<List<SamplingPreset>> {
        return flow {
            emit(buildList {
                val generalPreset = SamplingPreset(
                    id = "general_preset",
                    name = "General",
                    description = "This is a general preset",
                    minP = MinP(value = 0.05f),
                    temperature = Temperature(value = 0.7f),
                    topP = TopP(value = 0.9f),
                    mirostat = Mirostat(value = 0u),
                    repeatPenalty = RepeatPenalty(value = 1.1f),
                    topK = TopK(value = 40),
                )

                val codingPreset = SamplingPreset(
                    id = "coding_preset",
                    name = "Coding",
                    description = "This is a coding preset",
                    minP = MinP(value = 0.05f),
                    temperature = Temperature(value = 0.2f),
                    topP = TopP(value = 0.9f),
                    mirostat = Mirostat(value = 0u),
                    repeatPenalty = RepeatPenalty(value = 1.05f),
                    topK = TopK(value = 40),
                )

                val codingAltPreset = SamplingPreset(
                    id = "coding_alt_preset",
                    name = "Coding Alt",
                    description = "This is a coding preset",
                    minP = MinP(value = 0.9f),
                    temperature = Temperature(value = 0.2f),
                    topP = TopP(value = 1f),
                    mirostat = Mirostat(value = 0u),
                    repeatPenalty = RepeatPenalty(value = 1.05f),
                    topK = TopK(value = 0),
                )

                val factualPrecisePreset = SamplingPreset(
                    id = "factual_precise_preset",
                    name = "Factual/Precise",
                    description = "This is a factual/precise preset",
                    minP = MinP(value = 0.1f),
                    temperature = Temperature(value = 0.3f),
                    topP = TopP(value = 8f),
                    mirostat = Mirostat(value = 0u),
                    repeatPenalty = RepeatPenalty(value = 1.05f),
                    topK = TopK(value = 0),
                )

                val creativeWriting = SamplingPreset(
                    id = "creative_writing_preset",
                    name = "Creative writing",
                    description = "This is a creative writing preset",
                    minP = MinP(value = 0.05f),
                    temperature = Temperature(value = 1f),
                    topP = TopP(value = .95f),
                    mirostat = Mirostat(value = 0u),
                    repeatPenalty = RepeatPenalty(value = 1f),
                    topK = TopK(value = 0),
                )

                val creativeChat = SamplingPreset(
                    id = "creative_chat_preset",
                    name = "Creative chat",
                    description = "This is a creative chat preset",
                    minP = MinP(value = 0.05f),
                    temperature = Temperature(value = .85f),
                    topP = TopP(value = .95f),
                    mirostat = Mirostat(value = 0u),
                    repeatPenalty = RepeatPenalty(value = 1.15f),
                    topK = TopK(value = 0),
                )

                add(generalPreset)
                add(codingPreset)
                add(codingAltPreset)
                add(factualPrecisePreset)
                add(creativeWriting)
                add(creativeChat)
            })
        }
    }
}