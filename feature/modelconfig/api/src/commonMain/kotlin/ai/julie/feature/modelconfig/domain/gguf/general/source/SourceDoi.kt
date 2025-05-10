package ai.julie.feature.modelconfig.domain.gguf.general.source

data class SourceDoi(
    /**
     * Source Digital Object Identifier (DOI) https://www.doi.org/
     */
    override val value: String,
) : GeneralSourceInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.source.doi"
    }
}