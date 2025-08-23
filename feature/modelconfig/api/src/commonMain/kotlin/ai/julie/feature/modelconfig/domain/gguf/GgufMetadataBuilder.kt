package ai.julie.feature.modelconfig.domain.gguf

/**
 * Common interface for all GGUF metadata builders.
 * Provides a unified API for setting fields and building metadata instances.
 */
interface GgufMetadataBuilder {
    /**
     * Set a field based on the key and value from GGUF metadata
     */
    fun setField(key: String, value: Any): GgufMetadataBuilder
    
    /**
     * Build the GgufMetadata instance
     */
    fun build(): GgufMetadata
}