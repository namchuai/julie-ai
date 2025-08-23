package ai.julie.feature.modelconfig.domain.gguf.general.source

data class BaseModelRepoUrl(
    val id: String,
    /**
     * URL to the source of the parent model's repository such as a GitHub repo or HuggingFace repo
     */
    override val value: String,
) : GeneralSourceInfo {
    override val key: String = "general.base_model.$id.repo_url"
}