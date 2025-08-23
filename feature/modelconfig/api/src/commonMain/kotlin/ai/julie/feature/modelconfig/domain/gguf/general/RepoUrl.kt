package ai.julie.feature.modelconfig.domain.gguf.general

data class RepoUrl(
    /**
     * URL to the model's repository such as a GitHub repo or HuggingFace repo
     */
    override val value: String,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.repo_url"
    }
}