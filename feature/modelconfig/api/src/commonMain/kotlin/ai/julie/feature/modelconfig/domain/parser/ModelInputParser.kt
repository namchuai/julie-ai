package ai.julie.feature.modelconfig.domain.parser

import ai.julie.feature.message.domain.model.EnrichedMessage
import ai.julie.feature.modelconfig.domain.gguf.GgufMetadata

interface ModelInputParser {
    fun parse(
        messages: List<EnrichedMessage>,
        metadata: GgufMetadata,
    ): String
}