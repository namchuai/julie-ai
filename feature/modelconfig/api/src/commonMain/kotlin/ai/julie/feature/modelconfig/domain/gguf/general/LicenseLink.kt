package ai.julie.feature.modelconfig.domain.gguf.general

data class LicenseLink(
    /**
     * URL to the license.
     */
    override val value: String,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.license.link"
    }
}