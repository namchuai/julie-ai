package ai.julie.feature.modelconfig.domain.gguf.general

data class Datasets(
    /**
     * Links or references to datasets that the model was trained upon
     */
    override val value: List<String>,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.datasets"
    }
}