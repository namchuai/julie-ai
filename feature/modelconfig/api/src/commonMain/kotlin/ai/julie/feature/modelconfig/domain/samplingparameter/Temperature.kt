package ai.julie.feature.modelconfig.domain.samplingparameter

data class Temperature(
    override val value: Float,
) : SamplingParameter<Float> {
    override val key: String = KEY

    companion object {
        const val KEY = "temperature"
    }
}
