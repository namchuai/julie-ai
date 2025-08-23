package ai.julie.feature.modelconfig.domain.gguf

import ai.julie.feature.modelconfig.domain.gguf.general.Architecture
import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture
import ai.julie.feature.modelconfig.domain.gguf.deepseek2.Deepseek2ModelMetadata
import ai.julie.feature.modelconfig.domain.gguf.qwen3.Qwen3MoeModelMetadata
import ai.julie.logging.Logger
import kotbase.Document

object GgufMetadataFactory {

    private const val TAG = "GgufMetadataFactory"

    fun fromDocument(document: Document): GgufMetadata {
        val architectureValue = document.getString(Architecture.KEY)
            ?: throw IllegalArgumentException("Document must contain architecture field")

        Logger.d("[$TAG] Architecture value from document: $architectureValue")
        
        // Find architecture by matching the value field (lowercase) rather than enum name
        val architecture = SupportedArchitecture.entries.find { it.value == architectureValue }
            ?: throw IllegalArgumentException("Unsupported architecture: $architectureValue")

        Logger.d("[$TAG] Parsed architecture: $architecture")
        Logger.d("[$TAG] Starting conversion to ${architecture.name} metadata")

        return when (architecture) {
            SupportedArchitecture.LLAMA -> LlamaModelMetadata.fromDocument(document)
            SupportedArchitecture.QWEN3MOE -> Qwen3MoeModelMetadata.fromDocument(document)
            SupportedArchitecture.DEEPSEEK2 -> Deepseek2ModelMetadata.fromDocument(document)

            else -> throw IllegalArgumentException("Architecture $architecture not yet implemented")
        }
    }
}