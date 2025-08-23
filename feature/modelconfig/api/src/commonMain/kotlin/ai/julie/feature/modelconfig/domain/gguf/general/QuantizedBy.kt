package ai.julie.feature.modelconfig.domain.gguf.general

data class QuantizedBy(
    /**
     * The name of the individual who quantized the model
     */
    override val value: String,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.quantized_by"
    }
}