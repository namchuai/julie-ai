package ai.julie.feature.modelconfig.domain.modelloadingsetting

data class UseMemoryMapping(
    override val value: Boolean,
) : ModelLoadingSetting {

    override val key: String = KEY

    companion object {
        const val KEY = "use_mmap"
    }
}
