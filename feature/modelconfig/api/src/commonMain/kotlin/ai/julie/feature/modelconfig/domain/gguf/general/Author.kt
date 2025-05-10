package ai.julie.feature.modelconfig.domain.gguf.general

data class Author(
    /**
     * The author of the model.
     */
    override val value: String,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.author"
    }
}