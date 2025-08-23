package ai.julie.feature.modelconfig.domain.setting

data class AddGenerationPrompt(
    override val value: Boolean,
) : ModelSettingItem {

    override val key: String = KEY

    companion object {
        const val KEY = "add_generation_prompt"
    }
}