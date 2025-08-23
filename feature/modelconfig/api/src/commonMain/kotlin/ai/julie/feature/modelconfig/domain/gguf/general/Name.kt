package ai.julie.feature.modelconfig.domain.gguf.general

data class Name(
    /**
     * The name of the model. This should be a human-readable name that can be used to identify the
     * model. It should be unique within the community that the model is defined in.
     *
     */
    override val value: String,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.name"
    }
}