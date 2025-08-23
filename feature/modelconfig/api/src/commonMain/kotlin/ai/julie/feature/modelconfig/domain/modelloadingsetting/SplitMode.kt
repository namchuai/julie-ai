package ai.julie.feature.modelconfig.domain.modelloadingsetting

enum class SplitModeType(val value: UInt) {
    /** Single GPU - no splitting */
    NONE(0u),

    /** Split layers and KV across GPUs */
    LAYER(1u),

    /** Split layers and KV across GPUs, use tensor parallelism if supported */
    ROW(2u),
}

data class SplitMode(
    override val value: SplitModeType,
) : ModelLoadingSetting {

    override val key: String = KEY

    companion object {
        const val KEY = "split_mode"
    }
}
