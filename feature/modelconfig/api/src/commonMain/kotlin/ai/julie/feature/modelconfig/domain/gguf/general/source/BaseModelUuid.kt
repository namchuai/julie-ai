package ai.julie.feature.modelconfig.domain.gguf.general.source

data class BaseModelUuid(
    val id: String,
    /**
     * Parent Universally unique identifier
     */
    override val value: String,
) : GeneralSourceInfo {
    override val key: String = "general.base_model.$id.uuid"
}