package ai.julie.feature.modelconfig.domain.gguf.general.source

data class SourceRepoUrl(
    /**
     * URL to the source of the model's repository such as a GitHub repo or HuggingFace repo
     */
    override val value: String,
) : GeneralSourceInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.source.repo_url"
    }
}