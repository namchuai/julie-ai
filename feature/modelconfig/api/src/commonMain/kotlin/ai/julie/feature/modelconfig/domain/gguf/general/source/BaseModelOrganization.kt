package ai.julie.feature.modelconfig.domain.gguf.general.source

data class BaseModelOrganization(
    val id: String,
    /**
     * The organization of the parent model.
     */
    override val value: String,
) : GeneralSourceInfo {
    override val key: String = "general.base_model.$id.organization"
}