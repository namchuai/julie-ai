#include <jni.h>      // Need JNI types
#include "llama.h"    // Need llama types
#include <cstdio>      // For printf/fflush

// Function definition needs JNIEnv, jobject, llama_model_params
llama_model_params model_params_from_java(JNIEnv *env, jobject jparams)
{
    llama_model_params params = llama_model_default_params(); // Start with defaults

    jclass params_class = env->GetObjectClass(jparams);
    if (params_class == nullptr)
    {
        printf("[llama_jni_common.cpp] Error: Could not find LlamaModelParams class\n"); // Log prefix updated
        fflush(stdout);
        return params;
    }

    // Get field IDs (cache these in a real application for performance)
    jfieldID n_gpu_layers_field = env->GetFieldID(params_class, "nGpuLayers", "I");
    // jfieldID split_mode_field = env->GetFieldID(params_class, "splitMode", "I"); // Commented out - Field doesn't exist in Kotlin
    jfieldID main_gpu_field = env->GetFieldID(params_class, "mainGpu", "I");
    jfieldID vocab_only_field = env->GetFieldID(params_class, "vocabOnly", "Z");
    jfieldID use_mmap_field = env->GetFieldID(params_class, "useMmap", "Z");
    jfieldID use_mlock_field = env->GetFieldID(params_class, "useMlock", "Z");
    jfieldID check_tensors_field = env->GetFieldID(params_class, "checkTensors", "Z");

    // Get field values
    if (n_gpu_layers_field)
        params.n_gpu_layers = env->GetIntField(jparams, n_gpu_layers_field);
    // if (split_mode_field) // Commented out - Field doesn't exist in Kotlin
    //     params.split_mode = static_cast<enum llama_split_mode>(env->GetIntField(jparams, split_mode_field));
    if (main_gpu_field)
        params.main_gpu = env->GetIntField(jparams, main_gpu_field);
    if (vocab_only_field)
        params.vocab_only = env->GetBooleanField(jparams, vocab_only_field);
    if (use_mmap_field)
        params.use_mmap = env->GetBooleanField(jparams, use_mmap_field);
    if (use_mlock_field)
        params.use_mlock = env->GetBooleanField(jparams, use_mlock_field);
    if (check_tensors_field)
        params.check_tensors = env->GetBooleanField(jparams, check_tensors_field);

    // tensor_split, kv_overrides, callbacks etc. are omitted

    env->DeleteLocalRef(params_class);
    return params;
}

// Helper function to convert Kotlin LlamaContextParams to C llama_context_params
// NOTE: Only includes fields easily mapped from Kotlin. Callbacks and complex types omitted.
llama_context_params context_params_from_java(JNIEnv *env, jobject jparams)
{
    llama_context_params params = llama_context_default_params(); // Start with defaults

    jclass params_class = env->GetObjectClass(jparams);
    if (params_class == nullptr)
    {
        printf("[llama_jni_common.cpp] Error: Could not find LlamaContextParams class\n"); // Log prefix updated
        fflush(stdout);
        return params;
    }

    // Get field IDs
    jfieldID n_ctx_field = env->GetFieldID(params_class, "nCtx", "I");
    jfieldID n_batch_field = env->GetFieldID(params_class, "nBatch", "I");
    jfieldID n_ubatch_field = env->GetFieldID(params_class, "nUbatch", "I");
    jfieldID n_seq_max_field = env->GetFieldID(params_class, "nSeqMax", "I");
    jfieldID n_threads_field = env->GetFieldID(params_class, "nThreads", "I");
    jfieldID n_threads_batch_field = env->GetFieldID(params_class, "nThreadsBatch", "I");
    // jfieldID rope_scaling_type_field = env->GetFieldID(params_class, "ropeScalingType", "I"); // Missing in Kotlin
    // jfieldID pooling_type_field = env->GetFieldID(params_class, "poolingType", "I");          // Missing in Kotlin
    // jfieldID attention_type_field = env->GetFieldID(params_class, "attentionType", "I");      // Missing in Kotlin
    jfieldID rope_freq_base_field = env->GetFieldID(params_class, "ropeFreqBase", "F");
    jfieldID rope_freq_scale_field = env->GetFieldID(params_class, "ropeFreqScale", "F");
    // jfieldID yarn_ext_factor_field = env->GetFieldID(params_class, "yarnExtFactor", "F"); // Missing in Kotlin
    // jfieldID yarn_attn_factor_field = env->GetFieldID(params_class, "yarnAttnFactor", "F"); // Missing in Kotlin
    // jfieldID yarn_beta_fast_field = env->GetFieldID(params_class, "yarnBetaFast", "F"); // Missing in Kotlin
    // jfieldID yarn_beta_slow_field = env->GetFieldID(params_class, "yarnBetaSlow", "F"); // Missing in Kotlin
    // jfieldID yarn_orig_ctx_field = env->GetFieldID(params_class, "yarnOrigCtx", "I"); // Missing in Kotlin
    // jfieldID defrag_thold_field = env->GetFieldID(params_class, "defragThold", "F"); // Missing in Kotlin
    jfieldID embeddings_field = env->GetFieldID(params_class, "embeddings", "Z");
    jfieldID offload_kqv_field = env->GetFieldID(params_class, "offloadKqv", "Z");
    jfieldID flash_attn_field = env->GetFieldID(params_class, "flashAttn", "Z");
    jfieldID no_perf_field = env->GetFieldID(params_class, "noPerf", "Z");

    // Get field values
    if (n_ctx_field)
        params.n_ctx = env->GetIntField(jparams, n_ctx_field);
    if (n_batch_field)
        params.n_batch = env->GetIntField(jparams, n_batch_field);
    if (n_ubatch_field)
        params.n_ubatch = env->GetIntField(jparams, n_ubatch_field);
    if (n_seq_max_field)
        params.n_seq_max = env->GetIntField(jparams, n_seq_max_field);
    if (n_threads_field)
        params.n_threads = env->GetIntField(jparams, n_threads_field);
    if (n_threads_batch_field)
        params.n_threads_batch = env->GetIntField(jparams, n_threads_batch_field);
    // if (rope_scaling_type_field) // Missing in Kotlin
    //     params.rope_scaling_type = static_cast<enum llama_rope_scaling_type>(env->GetIntField(jparams, rope_scaling_type_field));
    // if (pooling_type_field) // Missing in Kotlin
    //     params.pooling_type = static_cast<enum llama_pooling_type>(env->GetIntField(jparams, pooling_type_field));
    // if (attention_type_field) // Missing in Kotlin
    //     params.attention_type = static_cast<enum llama_attention_type>(env->GetIntField(jparams, attention_type_field));
    if (rope_freq_base_field)
        params.rope_freq_base = env->GetFloatField(jparams, rope_freq_base_field);
    if (rope_freq_scale_field)
        params.rope_freq_scale = env->GetFloatField(jparams, rope_freq_scale_field);
    // if (yarn_ext_factor_field) // Missing in Kotlin
    //     params.yarn_ext_factor = env->GetFloatField(jparams, yarn_ext_factor_field);
    // if (yarn_attn_factor_field) // Missing in Kotlin
    //     params.yarn_attn_factor = env->GetFloatField(jparams, yarn_attn_factor_field);
    // if (yarn_beta_fast_field) // Missing in Kotlin
    //     params.yarn_beta_fast = env->GetFloatField(jparams, yarn_beta_fast_field);
    // if (yarn_beta_slow_field) // Missing in Kotlin
    //     params.yarn_beta_slow = env->GetFloatField(jparams, yarn_beta_slow_field);
    // if (yarn_orig_ctx_field) // Missing in Kotlin
    //     params.yarn_orig_ctx = env->GetIntField(jparams, yarn_orig_ctx_field);
    // if (defrag_thold_field) // Missing in Kotlin
    //     params.defrag_thold = env->GetFloatField(jparams, defrag_thold_field);
    if (embeddings_field)
        params.embeddings = env->GetBooleanField(jparams, embeddings_field);
    if (offload_kqv_field)
        params.offload_kqv = env->GetBooleanField(jparams, offload_kqv_field);
    if (flash_attn_field)
        params.flash_attn = env->GetBooleanField(jparams, flash_attn_field);
    if (no_perf_field)
        params.no_perf = env->GetBooleanField(jparams, no_perf_field);

    // cb_eval, type_k, type_v, logits_all (deprecated), abort_callback are omitted

    env->DeleteLocalRef(params_class);
    return params;
} 