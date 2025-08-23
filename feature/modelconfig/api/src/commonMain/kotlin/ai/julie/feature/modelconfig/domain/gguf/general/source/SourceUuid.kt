package ai.julie.feature.modelconfig.domain.gguf.general.source

data class SourceUuid(
    /**
     * Source Universally unique identifier
     */
    override val value: String,
) : GeneralSourceInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.source.uuid"
    }
}