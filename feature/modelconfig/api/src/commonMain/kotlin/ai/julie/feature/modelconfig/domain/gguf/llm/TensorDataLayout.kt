package ai.julie.feature.modelconfig.domain.gguf.llm

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class TensorDataLayout(
    val architecture: SupportedArchitecture,
    val value: String,
) {
    val key: String = "${architecture.value}.tensor_data_layout"
    
    /**
     * When a model is converted to GGUF, tensors may be rearranged to improve performance. 
     * This key describes the layout of the tensor data. This is not required; if not present, 
     * it is assumed to be `reference`.
     * 
     * - `reference`: tensors are laid out in the same order as the original model
     * - further options can be found for each architecture in their respective sections
     */
}