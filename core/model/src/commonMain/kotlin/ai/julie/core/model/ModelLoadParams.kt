package ai.julie.core.model

data class ModelLoadParams(
    val nGpuLayers: Int = 0,
    val mainGpu: Int = 0,
    val vocabOnly: Boolean = false,
    val useMmap: Boolean = true,
    val useMlock: Boolean = false,
    val checkTensors: Boolean = false
)
