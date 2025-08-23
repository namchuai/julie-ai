package ai.julie.feature.modelmanagement.ui.modelconfig

import ai.julie.core.common.AsyncState
import ai.julie.core.model.ModelLoadParams
import ai.julie.feature.modelconfig.domain.preset.SamplingPreset

data class ModelConfigState(
    val samplingPresetState: AsyncState<SamplingPresetData>,
    val modelContextState: AsyncState<ModelContextData>,
    val modelLoadState: AsyncState<ModelLoadData>,
)

data class SamplingPresetData(
    val samplingPresetList: List<SamplingPreset>,
    val selectedSamplingPreset: SamplingPreset,
)

data class ModelContextData(
    val contextLength: ContextLengthParam,
    val nBatch: ContextLengthParam,
    val nUBatch: ContextLengthParam,
)

data class ContextLengthParam(
    val min: Int = 1,
    val max: ULong,
    val step: Int = 1,
    val value: ULong,
)

data class ModelLoadData(
    val currentParams: ModelLoadParams,
    val pendingReload: Boolean = false,
)

data class GpuLayersParam(
    val min: Int = 0,
    val max: Int = 100,
    val step: Int = 1,
    val value: Int,
)
