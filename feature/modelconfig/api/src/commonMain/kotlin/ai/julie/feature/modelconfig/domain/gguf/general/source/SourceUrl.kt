package ai.julie.feature.modelconfig.domain.gguf.general.source

data class SourceUrl(
    /**
     * URL to the source of the model's homepage. This can be a GitHub repo, a paper, etc.
     */
    override val value: String,
) : GeneralSourceInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.source.url"
    }
}