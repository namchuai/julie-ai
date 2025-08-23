package ai.julie.feature.modelconfig.domain.samplingparameter

data class RepeatPenalty(
    override val value: Float,
) : SamplingParameter<Float> {
    override val key: String = KEY

    companion object {
        const val KEY = "repeat_penalty"
    }
}
