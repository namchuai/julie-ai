package ai.julie.feature.modelconfig.domain.gguf.general.source

data class BaseModelCount(
    /**
     * Number of parent models
     */
    override val value: UInt,
) : GeneralSourceInfo {
    override val key: String = KEY

    companion object {
        const val KEY = "general.base_model.count"
    }
}