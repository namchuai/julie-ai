package ai.julie.feature.modelconfig.domain.gguf.general

data class License(
    /**
     * License of the model, expressed as a SPDX license expression (e.g. "MIT OR Apache-2.0).
     * Do not include any other information, such as the license text or the URL to the license.
     */
    override val value: String,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.license"
    }
}