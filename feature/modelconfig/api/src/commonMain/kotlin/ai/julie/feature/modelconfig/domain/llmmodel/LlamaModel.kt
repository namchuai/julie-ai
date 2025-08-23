package ai.julie.feature.modelconfig.domain.llmmodel

import ai.julie.feature.modelconfig.domain.gguf.LlamaModelMetadata
import ai.julie.feature.modelconfig.domain.parser.LlamaInputParser
import ai.julie.feature.modelconfig.domain.parser.LlamaOutputParser

class LlamaModel(
    override val id: String,
    override val title: String,
    override val metadata: LlamaModelMetadata,
) : GgufModel {

    override val inputParser = LlamaInputParser()

    override val outputParser = LlamaOutputParser()
}