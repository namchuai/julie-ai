package ai.julie.feature.modelconfig.domain.samplingparameter

data class ContextLength(
    override val value: ULong,
) : SamplingParameter<ULong> {
    override val key: String = KEY

    companion object {
        const val KEY = "context_length"
    }
}
