package ai.julie.feature.modelconfig.domain.gguf.general

data class Description(
    /**
     * Free-form description of the model including anything that isn't covered by the other fields
     */
    override val value: String,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.description"
    }
}