package ai.julie.feature.modelconfig.domain.modelloadingsetting

data class VocabOnly(
    override val value: Boolean,
) : ModelLoadingSetting {

    override val key: String = KEY

    companion object {
        const val KEY = "vocab_only"
    }
}
