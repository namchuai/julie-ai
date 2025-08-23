package ai.julie.feature.modelconfig.domain.gguf.general.source

data class BaseModelDoi(
    val id: String,
    /**
     * Parent Digital Object Identifier (DOI) https://www.doi.org/
     */
    override val value: String,
) : GeneralSourceInfo {
    override val key: String = "general.base_model.$id.doi"
}