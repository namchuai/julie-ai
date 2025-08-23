package ai.julie.feature.modelconfig.domain.llmmodel

import ai.julie.feature.modelconfig.domain.gguf.GgufMetadata
import ai.julie.feature.modelconfig.domain.parser.ModelInputParser
import ai.julie.feature.modelconfig.domain.parser.ModelOutputParser

interface GgufModel {
    val id: String

    val title: String

    val metadata: GgufMetadata

    val inputParser: ModelInputParser

    val outputParser: ModelOutputParser
}
