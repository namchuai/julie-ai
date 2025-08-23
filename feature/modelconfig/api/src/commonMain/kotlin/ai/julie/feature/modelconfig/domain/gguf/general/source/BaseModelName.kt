package ai.julie.feature.modelconfig.domain.gguf.general.source

data class BaseModelName(
    val id: String,
    /**
     * The name of the parent model.
     */
    override val value: String,
) : GeneralSourceInfo {
    override val key: String = "general.base_model.$id.name"
}