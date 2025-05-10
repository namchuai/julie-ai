package ai.julie.feature.modelconfig.domain.gguf.llm

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class FeedForwardLength(
    val architecture: SupportedArchitecture,
    val value: ULong,
) {
    val key: String = "${architecture.value}.feed_forward_length"
    
    /**
     * Also known as `n_ff`. The length of the feed-forward layer.
     */
}