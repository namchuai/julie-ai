package ai.julie.feature.modelconfig.domain.modelcontextsetting

data class ContextLength(
    override val value: ULong,
) : ModelContextSetting {
    override val key: String = KEY

    companion object {
        const val KEY = "context_length"
    }
}
