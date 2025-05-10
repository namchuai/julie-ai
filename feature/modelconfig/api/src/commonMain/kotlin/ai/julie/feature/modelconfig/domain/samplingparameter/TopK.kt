package ai.julie.feature.modelconfig.domain.samplingparameter

data class TopK(
    override val value: Int,
) : SamplingParameter<Int> {
    override val key: String = KEY

    companion object {
        const val KEY = "top_k"
    }
}
