package ai.julie.feature.modelconfig.domain.gguf.general.source

data class BaseModelAuthor(
    val id: String,
    /**
     * The author of the parent model.
     */
    override val value: String,
) : GeneralSourceInfo {
    override val key: String = "general.base_model.$id.author"
}