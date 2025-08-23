package ai.julie.feature.modelconfig.domain.gguf.general.source

data class BaseModelVersion(
    val id: String,
    /**
     * The version of the parent model.
     */
    override val value: String,
) : GeneralSourceInfo {
    override val key: String = "general.base_model.$id.version"
}