package ai.julie.feature.modelconfig.domain.gguf.general

data class Version(
    /**
     * The version of the model.
     */
    override val value: String,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.version"
    }
}