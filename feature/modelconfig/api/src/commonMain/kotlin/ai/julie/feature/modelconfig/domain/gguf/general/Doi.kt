package ai.julie.feature.modelconfig.domain.gguf.general

data class Doi(
    /**
     * Digital Object Identifier (DOI) https://www.doi.org/
     */
    override val value: String,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.doi"
    }
}