package ai.julie.feature.modelconfig.domain.samplingparameter

data class MinP(
    override val value: Float,
) : SamplingParameter<Float> {
    override val key: String = KEY

    companion object {
        const val KEY = "min_p"
    }
}
