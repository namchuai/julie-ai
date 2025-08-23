package ai.julie.feature.modelconfig.domain.modelcontextsetting

data class NoOfBatch(
    override val value: ULong,
) : ModelContextSetting {
    override val key: String = KEY

    companion object Companion {
        const val KEY = "n_batch"
    }
}
