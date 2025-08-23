package ai.julie.feature.modelconfig.domain.modelloadingsetting

data class ModelLoadingConfiguration(
    // TODO: need to implement const struct llama_model_kv_override * kv_overrides;
    val checkTensors: CheckTensors? = null,
    val gpuId: GpuId? = null,
    val splitMode: SplitMode? = null,
    val useExtraBuffer: UseExtraBuffer? = null,
    val vocabOnly: VocabOnly? = null,
    val noOfGpuLayer: NoOfGpuLayer? = null,
    val useMemoryLock: UseMemoryLock? = null,
    val useMemoryMapping: UseMemoryMapping? = null,
)
