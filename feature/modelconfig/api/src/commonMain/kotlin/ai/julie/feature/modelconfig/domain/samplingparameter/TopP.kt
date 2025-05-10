package ai.julie.feature.modelconfig.domain.samplingparameter

data class TopP(
    override val value: Float,
) : SamplingParameter<Float> {
    override val key: String = KEY

    companion object {
        const val KEY = "top_p"
    }
}
