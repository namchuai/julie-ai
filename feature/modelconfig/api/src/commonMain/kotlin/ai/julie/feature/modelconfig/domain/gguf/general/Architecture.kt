package ai.julie.feature.modelconfig.domain.gguf.general

enum class SupportedArchitecture(val value: String) {
    LLAMA("llama"),
    QWEN3MOE("qwen3moe"),
    DEEPSEEK2("deepseek2"),
    MPT("mpt"),
    GPTNEOX("gptneox"),
    GPTJ("gptj"),
    GPT2("gpt2"),
    BLOOM("bloom"),
    FALCON("falcon"),
    MAMBA("mamba"),
    RWKV("rwkv"),
}

data class Architecture(
    /**
     * describes what architecture this model implements. All lowercase ASCII, with only [a-z0-9]+
     * characters allowed.
     */
    override val value: SupportedArchitecture,
) : GeneralInfo {
    override val key: String = KEY

    companion object {
        const val KEY = "general.architecture"
    }
}
