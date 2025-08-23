package ai.julie.feature.modelconfig.domain.gguf.general

data class Alignment(
    override val value: UInt,
) : GeneralInfo {
    override val key: String = KEY

    companion object {
        const val KEY = "general.alignment"
    }
}
