package ai.julie.feature.modelconfig.domain.gguf.general

data class FineTune(
    /**
     * What has the base model been optimized toward.
     */
    override val value: String,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object Companion {
        const val KEY = "general.finetune"
    }
}