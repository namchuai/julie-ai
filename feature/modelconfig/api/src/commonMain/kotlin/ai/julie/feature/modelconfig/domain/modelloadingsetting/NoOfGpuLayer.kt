package ai.julie.feature.modelconfig.domain.modelloadingsetting

data class NoOfGpuLayer(
    override val value: UInt,
) : ModelLoadingSetting {

    override val key: String = KEY

    companion object {
        const val KEY = "n_gpuLayer"
    }
}
