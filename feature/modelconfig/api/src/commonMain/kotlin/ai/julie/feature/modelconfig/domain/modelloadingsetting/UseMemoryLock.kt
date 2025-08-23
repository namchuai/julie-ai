package ai.julie.feature.modelconfig.domain.modelloadingsetting

data class UseMemoryLock(
    override val value: Boolean,
) : ModelLoadingSetting {

    override val key: String = KEY

    companion object {
        const val KEY = "use_mlock"
    }
}
