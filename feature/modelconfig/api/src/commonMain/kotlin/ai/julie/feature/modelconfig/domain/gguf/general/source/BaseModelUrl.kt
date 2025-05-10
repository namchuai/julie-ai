package ai.julie.feature.modelconfig.domain.gguf.general.source

data class BaseModelUrl(
    val id: String,
    /**
     * URL to the source of the parent model's homepage. This can be a GitHub repo, a paper, etc.
     */
    override val value: String,
) : GeneralSourceInfo {
    override val key: String = "general.base_model.$id.url"
}