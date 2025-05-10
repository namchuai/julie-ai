package ai.julie.llamabinding.model

import ai.julie.core.model.ModelLoadParams

data class LlamaModelParams(
    val nGpuLayers: Int = 0,
    val mainGpu: Int = 0,
    val vocabOnly: Boolean = false,
    val useMmap: Boolean = true,
    val useMlock: Boolean = false,
    val checkTensors: Boolean = false
) {
    companion object {
        fun from(params: ModelLoadParams): LlamaModelParams {
            return LlamaModelParams(
                nGpuLayers = params.nGpuLayers,
                mainGpu = params.mainGpu,
                vocabOnly = params.vocabOnly,
                useMmap = params.useMmap,
                useMlock = params.useMlock,
                checkTensors = params.checkTensors
            )
        }
    }
}
