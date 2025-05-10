package ai.julie.feature.modelconfig.domain.gguf.general

data class Url(
    /**
     * URL to the model's homepage. This can be a GitHub repo, a paper, etc.
     */
    override val value: String,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.url"
    }
}