package ai.julie.feature.modelconfig.domain.preset

import ai.julie.feature.modelconfig.domain.samplingparameter.MinP
import ai.julie.feature.modelconfig.domain.samplingparameter.Mirostat
import ai.julie.feature.modelconfig.domain.samplingparameter.RepeatPenalty
import ai.julie.feature.modelconfig.domain.samplingparameter.Temperature
import ai.julie.feature.modelconfig.domain.samplingparameter.TopK
import ai.julie.feature.modelconfig.domain.samplingparameter.TopP

data class SamplingPreset(
    val id: String,
    val name: String,
    val description: String,

    val repeatPenalty: RepeatPenalty,
    val temperature: Temperature,
    val topK: TopK,

    val topP: TopP,
    val minP: MinP,
    val mirostat: Mirostat,
)
