package ai.julie.feature.modelconfig.domain.modelloadingsetting

data class GpuId(
    override val value: UInt,
) : ModelLoadingSetting {

    override val key: String = KEY

    companion object {
        const val KEY = "main_gpu"
    }
}
