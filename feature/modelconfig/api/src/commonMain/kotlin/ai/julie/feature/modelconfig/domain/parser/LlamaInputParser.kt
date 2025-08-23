package ai.julie.feature.modelconfig.domain.parser

import ai.julie.feature.message.domain.model.EnrichedMessage
import ai.julie.feature.modelconfig.domain.gguf.GgufMetadata
import ai.julie.feature.modelconfig.domain.gguf.LlamaModelMetadata

class LlamaInputParser : ModelInputParser {
    override fun parse(
        messages: List<EnrichedMessage>,
        metadata: GgufMetadata,
    ): String {
        require(metadata is LlamaModelMetadata) { "Metadata must be of type LlamaModelMetadata" }
        // TODO: inject jinja service here, provide it's context to get the string out.
        return "" // TODO: implement this
    }
}