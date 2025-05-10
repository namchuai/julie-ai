package ai.julie.feature.modelconfig.domain.samplingparameter

data class Mirostat(
    override val value: UInt,
) : SamplingParameter<UInt> {
    override val key: String = KEY

    companion object Companion {
        const val KEY = "mirostat"
    }
}
