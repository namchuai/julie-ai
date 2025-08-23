package ai.julie.feature.modelconfig.domain.gguf.general

data class Tags(
    /**
     * List of tags that can be used as search terms for a search engine or social media
     */
    override val value: List<String>,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.tags"
    }
}