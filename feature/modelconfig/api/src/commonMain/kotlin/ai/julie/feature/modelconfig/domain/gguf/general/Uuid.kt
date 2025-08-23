package ai.julie.feature.modelconfig.domain.gguf.general

data class Uuid(
    /**
     * Universally unique identifier
     */
    override val value: String,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.uuid"
    }
}