package ai.julie.feature.modelconfig.domain.gguf.general

data class LicenseName(
    /**
     * Human friendly license name
     */
    override val value: String,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.license.name"
    }
}