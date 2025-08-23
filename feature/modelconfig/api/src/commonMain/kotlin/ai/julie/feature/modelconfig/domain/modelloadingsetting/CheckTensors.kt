package ai.julie.feature.modelconfig.domain.modelloadingsetting

data class CheckTensors(
    override val value: Boolean,
) : ModelLoadingSetting {

    override val key: String = KEY

    companion object Companion {
        const val KEY = "check_tensors"
    }
}
