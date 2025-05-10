package ai.julie.core.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Sampling parameters that correspond to llama.cpp sampler chain configuration
 * These are user-configurable settings that affect text generation quality
 */
@Serializable
data class LlamaSamplerSettings(
    // Core sampling parameters
    val temperature: Float = 0.8f,              // llama_sampler_init_temp
    val topK: Int = 40,                          // llama_sampler_init_top_k  
    val topP: Float = 0.9f,                      // llama_sampler_init_top_p
    val minP: Float = 0.05f,                     // llama_sampler_init_min_p
    val typicalP: Float = 1.0f,                  // llama_sampler_init_typical

    // Repetition penalties
    val penaltyRepeat: Float = 1.1f,             // llama_sampler_init_penalties (penalty_repeat)
    val frequencyPenalty: Float = 0.0f,               // llama_sampler_init_penalties (penalty_freq)
    val penaltyPresent: Float = 0.0f,            // llama_sampler_init_penalties (penalty_present)
    val penaltyLastN: Int = 64,                  // llama_sampler_init_penalties (penalty_last_n)

    // Mirostat sampling
    val mirostat: Int = 0,                       // 0=disabled, 1=mirostat, 2=mirostat_v2
    val mirostatTau: Float = 5.0f,               // target entropy
    val mirostatEta: Float = 0.1f,               // learning rate

    // Advanced sampling  
    val tfsZ: Float = 1.0f,                      // tail free sampling
    val seed: Int = -1,                          // -1 for random seed

    // Generation limits
    val maxTokens: Int = -1,                     // -1 for unlimited
    val stop: List<String> = emptyList(),        // stop sequences

    // Context configuration  
    val contextLength: Int = 8192                // context window size
)

/**
 * Parameter constraints based on model capabilities
 * Extracted from GGUF metadata to determine valid ranges
 */
@Serializable
data class LlamaSamplerConstraints(
    @Contextual val temperatureRange: ClosedRange<Float> = 0.1f..2.0f,
    @Contextual val topKRange: IntRange = 1..100,
    @Contextual val topPRange: ClosedRange<Float> = 0.0f..1.0f,
    @Contextual val minPRange: ClosedRange<Float> = 0.0f..1.0f,
    @Contextual val typicalPRange: ClosedRange<Float> = 0.0f..1.0f,
    @Contextual val penaltyRepeatRange: ClosedRange<Float> = 0.0f..2.0f,
    @Contextual val penaltyFreqRange: ClosedRange<Float> = 0.0f..2.0f,
    @Contextual val penaltyPresentRange: ClosedRange<Float> = 0.0f..2.0f,
    @Contextual val penaltyLastNRange: IntRange = 0..2048,
    @Contextual val mirostatTauRange: ClosedRange<Float> = 0.0f..10.0f,
    @Contextual val mirostatEtaRange: ClosedRange<Float> = 0.001f..1.0f,
    @Contextual val tfsZRange: ClosedRange<Float> = 0.0f..1.0f,
    val maxContextLength: Int = 2048,
    @Contextual val contextLengthRange: IntRange = 512..maxContextLength
)

/**
 * UI parameter configuration for dynamic forms
 */
@Serializable
data class SamplerParameterConfig<T : Comparable<T>>(
    val value: T,
    @Contextual val range: ClosedRange<T>? = null,
    @Contextual val intRange: IntRange? = null,
    val step: T? = null,
    val enabled: Boolean = true,
    val label: String,
    val description: String,
    val category: SamplerCategory
)

enum class SamplerCategory {
    CORE,           // temperature, top_k, top_p
    REPETITION,     // penalty parameters  
    ADVANCED,       // mirostat, tfs, etc.
    LIMITS,         // max_tokens, stop sequences
    TEMPLATE        // jinja templates
}