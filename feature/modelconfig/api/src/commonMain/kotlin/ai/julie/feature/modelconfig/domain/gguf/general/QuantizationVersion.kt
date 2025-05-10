package ai.julie.feature.modelconfig.domain.gguf.general

data class QuantizationVersion(
    /**
     * The version of the quantization format. Not required if the model is not quantized (i.e. no
     * tensors are quantized). If any tensors are quantized, this must be present.
     * This is separate to the quantization scheme of the tensors itself; the quantization version
     * may change without changing the scheme's name (e.g. the quantization scheme is Q5_K, and the
     * quantization version is 4).
     */
    override val value: UInt,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.quantization_version"
    }
}
