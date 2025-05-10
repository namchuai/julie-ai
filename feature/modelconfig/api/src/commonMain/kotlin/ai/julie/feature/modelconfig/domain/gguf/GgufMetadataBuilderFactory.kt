package ai.julie.feature.modelconfig.domain.gguf

import ai.julie.feature.modelconfig.domain.gguf.deepseek2.Deepseek2ModelMetadataBuilder
import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture
import ai.julie.feature.modelconfig.domain.gguf.qwen3.Qwen3MoeModelMetadataBuilder

/**
 * Factory for creating appropriate metadata builders based on architecture
 */
object GgufMetadataBuilderFactory {

    /**
     * Creates a metadata builder based on the architecture string.
     * If architecture is not provided, returns a default LLAMA builder.
     */
    fun createBuilder(architectureValue: String?): GgufMetadataBuilder {
        println("NamH architecture: $architectureValue")
        if (architectureValue == null) {
            // Default to LLAMA if no architecture is specified
            return LlamaModelMetadataBuilder.create()
        }

        // Find architecture by matching the value field (lowercase) rather than enum name
        val architecture = SupportedArchitecture.entries.find { it.value == architectureValue }

        return when (architecture) {
            SupportedArchitecture.LLAMA -> LlamaModelMetadataBuilder.create()
            SupportedArchitecture.QWEN3MOE -> Qwen3MoeModelMetadataBuilder.create()
            SupportedArchitecture.DEEPSEEK2 -> Deepseek2ModelMetadataBuilder.create()
            SupportedArchitecture.MPT,
            SupportedArchitecture.GPTNEOX,
            SupportedArchitecture.GPTJ,
            SupportedArchitecture.GPT2,
            SupportedArchitecture.BLOOM,
            SupportedArchitecture.FALCON,
            SupportedArchitecture.MAMBA,
            SupportedArchitecture.RWKV,
            null -> {
                // For unsupported architectures, default to LLAMA
                // This provides backwards compatibility and allows basic metadata parsing
                LlamaModelMetadataBuilder.create()
            }
        }
    }
}