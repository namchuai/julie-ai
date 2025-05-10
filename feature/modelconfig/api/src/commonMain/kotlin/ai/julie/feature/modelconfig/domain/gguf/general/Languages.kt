package ai.julie.feature.modelconfig.domain.gguf.general

data class Languages(
    /**
     * What languages can the model speak. Encoded as ISO 639 two letter codes
     */
    override val value: List<String>,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.languages"
    }
}