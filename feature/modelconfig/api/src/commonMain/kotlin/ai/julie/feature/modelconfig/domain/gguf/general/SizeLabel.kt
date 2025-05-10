package ai.julie.feature.modelconfig.domain.gguf.general

data class SizeLabel(
    /**
     * Size class of the model, such as number of weights and experts. (Useful for leader boards)
     */
    override val value: String,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.size_label"
    }
}