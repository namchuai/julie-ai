#include <jni.h>
#include <string>
#include <vector>
#include "llama.h"
#include <cstdio>    // For printf/fflush
#include <new>       // For std::nothrow
#include <limits>    // For std::numeric_limits
#include <algorithm> // For std::max_element

llama_model_params model_params_from_java(JNIEnv *env, jobject jparams);
llama_context_params context_params_from_java(JNIEnv *env, jobject jparams);

// Helper to get the llama_batch struct pointer
static inline llama_batch *get_batch_ptr(JNIEnv *env, jlong batch_ptr)
{
    if (batch_ptr == 0)
    {
        printf("[llama_jni.cpp] Error: Received null llama_batch pointer\n");
        fflush(stdout);
        return nullptr;
    }
    return reinterpret_cast<llama_batch *>(batch_ptr);
}

// Macro to generate the JNI function name
// We need two levels of macros to ensure the prefix macro expands before token pasting
#define JNI_METHOD_NAME_INTERNAL(prefix, name) Java_##prefix##_NativeMethods_##name
#define JNI_METHOD_NAME(prefix, name) JNI_METHOD_NAME_INTERNAL(prefix, name)
#define LLAMA_JNI_PREFIX ai_julie_llamabinding

extern "C"
{


    // --- Backend Initialization ---
    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1backend_1init)(JNIEnv *env, jobject thiz)
    {
        llama_backend_init();
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1backend_1free)(JNIEnv *env, jobject thiz)
    {
        llama_backend_free();
    }

    // --- Model Loading ---
    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1load_1from_1file)(
        JNIEnv *env,
        jobject thiz,
        jstring path_model,
        jobject params)
    {
        printf("[llama_jni.cpp] Entered JNI llama_model_load_from_file\n");
        fflush(stdout);
        const char *path_cstr = env->GetStringUTFChars(path_model, nullptr);
        if (path_cstr == nullptr)
        {
            printf("[llama_jni.cpp] Error: GetStringUTFChars failed for path\n");
            fflush(stdout);
            return 0;
        }
        printf("[llama_jni.cpp] Model path C string: %s\n", path_cstr);
        fflush(stdout);

        printf("[llama_jni.cpp] Converting model params from Java object...\n");
        fflush(stdout);
        llama_model_params model_params = model_params_from_java(env, params);
        printf("[llama_jni.cpp] Model params converted (e.g., use_mmap: %d)\n", model_params.use_mmap);
        fflush(stdout);

        printf("[llama_jni.cpp] Calling llama_model_load_from_file (C lib)...\n");
        fflush(stdout);
        llama_model *model = llama_model_load_from_file(path_cstr, model_params);
        printf("[llama_jni.cpp] llama_model_load_from_file (C lib) returned: %p\n", (void *)model);
        fflush(stdout);

        env->ReleaseStringUTFChars(path_model, path_cstr);
        printf("[llama_jni.cpp] Released path string\n");
        fflush(stdout);

        jlong result = reinterpret_cast<jlong>(model);
        printf("[llama_jni.cpp] Returning model pointer: %lld\n", result);
        fflush(stdout);
        return result;
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1free)(
        JNIEnv *env,
        jobject thiz,
        jlong model_ptr)
    {
        llama_model *model = reinterpret_cast<llama_model *>(model_ptr);
        llama_model_free(model);
    }

    // --- Context Management ---
    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1context_1init_1from_1model)(
        JNIEnv *env,
        jobject thiz,
        jlong model_ptr,
        jobject params)
    {
        llama_model *model = reinterpret_cast<llama_model *>(model_ptr);
        if (!model)
        {
            return 0; // Error: Invalid model pointer
        }

        llama_context_params context_params = context_params_from_java(env, params);

        llama_context *ctx = llama_init_from_model(model, context_params);

        return reinterpret_cast<jlong>(ctx);
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1context_1free)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        llama_free(ctx);
    }

    // --- Context Info ---
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1n_1ctx)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        return llama_n_ctx(ctx);
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1n_1batch)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        return ctx ? llama_n_batch(ctx) : 0;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1n_1ubatch)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        return ctx ? llama_n_ubatch(ctx) : 0;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1n_1seq_1max)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        return ctx ? llama_n_seq_max(ctx) : 0;
    }

    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1get_1model)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        return reinterpret_cast<jlong>(llama_get_model(ctx));
    }

    // --- Model Info ---
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1n_1vocab)(
        JNIEnv *env,
        jobject thiz,
        jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return 0;
        const llama_vocab *vocab = llama_model_get_vocab(model); // Use llama_model_get_vocab
        return vocab ? llama_vocab_n_tokens(vocab) : 0;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1n_1ctx_1train)(
        JNIEnv *env,
        jobject thiz,
        jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr); // Changed to const
        return llama_model_n_ctx_train(model);
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1n_1embd)(
        JNIEnv *env,
        jobject thiz,
        jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr); // Changed to const
        return llama_model_n_embd(model);
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1desc)(
        JNIEnv *env,
        jobject thiz,
        jlong model_ptr,
        jbyteArray buffer,
        jint buffer_size)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr); // Changed to const
        if (!model)
            return -1;

        jbyte *buf_ptr = env->GetByteArrayElements(buffer, nullptr);
        if (!buf_ptr)
            return -2; // Error getting buffer elements

        int result = llama_model_desc(model, reinterpret_cast<char *>(buf_ptr), static_cast<size_t>(buffer_size));

        env->ReleaseByteArrayElements(buffer, buf_ptr, 0); // 0 = copy back and free

        return result;
    }

    // --- Batch Management --- NEW
    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1batch_1init)(
        JNIEnv *env,
        jobject thiz,
        jint n_tokens,
        jint embd,
        jint n_seq_max)
    {
        printf("[llama_jni.cpp] llama_batch_init(n_tokens=%d, embd=%d, n_seq_max=%d)\n", n_tokens, embd, n_seq_max);
        fflush(stdout);
        llama_batch batch = llama_batch_init(n_tokens, embd, n_seq_max);
        llama_batch *batch_ptr = new (std::nothrow) llama_batch(batch);
        if (!batch_ptr)
        {
            printf("[llama_jni.cpp] Error: Failed to allocate memory for llama_batch\n");
            fflush(stdout);
            return 0;
        }
        printf("[llama_jni.cpp] llama_batch_init created pointer: %p\n", (void *)batch_ptr);
        fflush(stdout);
        return reinterpret_cast<jlong>(batch_ptr);
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1batch_1free)(
        JNIEnv *env,
        jobject thiz,
        jlong batch_ptr)
    {
        printf("[llama_jni.cpp] llama_batch_free called for pointer: %p\n", (void *)batch_ptr);
        fflush(stdout);
        llama_batch *batch = get_batch_ptr(env, batch_ptr);
        if (batch)
        {
            llama_batch_free(*batch);
            delete batch;
            printf("[llama_jni.cpp] llama_batch freed.\n");
            fflush(stdout);
        }
        else
        {
            printf("[llama_jni.cpp] llama_batch_free called with null pointer.\n");
            fflush(stdout);
        }
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1batch_1clear)(
        JNIEnv *env,
        jobject thiz,
        jlong batch_ptr)
    {
        llama_batch *batch = get_batch_ptr(env, batch_ptr);
        if (batch)
        {
            batch->n_tokens = 0;
        }
        else
        {
            printf("[llama_jni.cpp] llama_batch_clear called with null pointer.\n");
            fflush(stdout);
        }
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1batch_1add)(
        JNIEnv *env,
        jobject thiz,
        jlong batch_ptr,
        jint token,
        jint pos,
        jintArray seq_ids_array,
        jboolean logits)
    {
        // Functionality removed - llama_batch_add does not exist in included llama.h
        printf("[llama_jni.cpp] WARN: JNI llama_batch_add called, but underlying function is missing!\n");
        fflush(stdout);
        /*
        llama_batch* batch = get_batch_ptr(env, batch_ptr);
        if (!batch) return;
        jsize seq_id_count = env->GetArrayLength(seq_ids_array);
        if (seq_id_count < 1) {
            printf("[llama_jni.cpp] Error: llama_batch_add JNI expects seq_ids array with at least 1 element\n"); fflush(stdout);
            return;
        }
        jint seq_id_native[1];
        env->GetIntArrayRegion(seq_ids_array, 0, 1, seq_id_native);
        // Call the C function (assuming it exists and signature matches)
        // llama_batch_add(*batch, static_cast<llama_token>(token), static_cast<llama_pos>(pos), 1, seq_id_native, static_cast<bool>(logits));
        */
    }

    // --- Batch Management Helpers (Manual Manipulation) --- NEW

    // Helper to ensure index is within bounds and batch pointer is valid
    static inline bool check_batch_index(JNIEnv *env, llama_batch *batch, jint index, const char *func_name)
    {
        if (!batch)
        {
            printf("[llama_jni.cpp] Error in %s: Received null llama_batch pointer\n", func_name);
            fflush(stdout);
            return false;
        }
        // Assuming n_tokens field in llama_batch struct indicates allocated size, which might not be true.
        // The actual allocated size depends on llama_batch_init call. Need a way to know the max allocated size.
        // For now, we skip upper bound check, assuming Kotlin side respects the size from llama_batch_init.
        // A safer approach would store the allocated size separately when batch is init.
        if (index < 0)
        {
            printf("[llama_jni.cpp] Error in %s: Index %d out of bounds (must be >= 0)\n", func_name, index);
            fflush(stdout);
            return false;
        }
        // Add upper bound check if max size is known
        // if (index >= max_allocated_size) { ... }
        return true;
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1batch_1set_1n_1tokens)(
        JNIEnv *env,
        jobject thiz,
        jlong batch_ptr,
        jint n_tokens)
    {
        llama_batch *batch = get_batch_ptr(env, batch_ptr);
        if (batch)
        {
            // TODO: Add check: n_tokens should not exceed allocated size
            batch->n_tokens = static_cast<int32_t>(n_tokens);
        }
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1batch_1set_1token)(
        JNIEnv *env,
        jobject thiz,
        jlong batch_ptr,
        jint index,
        jint token)
    {
        llama_batch *batch = get_batch_ptr(env, batch_ptr);
        if (check_batch_index(env, batch, index, "llama_batch_set_token") && batch->token)
        {
            batch->token[index] = static_cast<llama_token>(token);
        }
        else if (batch && !batch->token)
        {
            printf("[llama_jni.cpp] Error in llama_batch_set_token: batch->token is null (batch likely init with embd != 0)\n");
            fflush(stdout);
        }
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1batch_1set_1pos)(
        JNIEnv *env,
        jobject thiz,
        jlong batch_ptr,
        jint index,
        jint pos)
    {
        llama_batch *batch = get_batch_ptr(env, batch_ptr);
        if (check_batch_index(env, batch, index, "llama_batch_set_pos") && batch->pos)
        {
            batch->pos[index] = static_cast<llama_pos>(pos);
        }
        else if (batch && !batch->pos)
        {
            printf("[llama_jni.cpp] Error in llama_batch_set_pos: batch->pos is null\n");
            fflush(stdout);
        }
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1batch_1set_1seq_1id)(
        JNIEnv *env,
        jobject thiz,
        jlong batch_ptr,
        jint index,
        jint seq_id)
    {
        llama_batch *batch = get_batch_ptr(env, batch_ptr);
        if (check_batch_index(env, batch, index, "llama_batch_set_seq_id") && batch->n_seq_id && batch->seq_id && batch->seq_id[index])
        {
            batch->n_seq_id[index] = 1; // Assuming only one seq_id per token for simplicity
            batch->seq_id[index][0] = static_cast<llama_seq_id>(seq_id);
        }
        else if (batch && (!batch->n_seq_id || !batch->seq_id || !batch->seq_id[index]))
        {
            printf("[llama_jni.cpp] Error in llama_batch_set_seq_id: batch->n_seq_id or batch->seq_id pointers are null or invalid for index %d\n", index);
            fflush(stdout);
        }
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1batch_1set_1logits)(
        JNIEnv *env,
        jobject thiz,
        jlong batch_ptr,
        jint index,
        jboolean logits)
    {
        llama_batch *batch = get_batch_ptr(env, batch_ptr);
        if (check_batch_index(env, batch, index, "llama_batch_set_logits") && batch->logits)
        {
            batch->logits[index] = static_cast<int8_t>(logits);
        }
        else if (batch && !batch->logits)
        {
            printf("[llama_jni.cpp] Error in llama_batch_set_logits: batch->logits is null\n");
            fflush(stdout);
        }
    }

    // --- NEW: Get batch n_tokens ---
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1batch_1get_1n_1tokens)(
        JNIEnv *env,
        jobject thiz,
        jlong batch_ptr)
    {
        llama_batch *batch = get_batch_ptr(env, batch_ptr);
        return batch ? batch->n_tokens : 0;
    }

    // --- Decoding/Evaluation ---
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1decode)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jlong batch_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        llama_batch *batch = get_batch_ptr(env, batch_ptr);
        if (!ctx || !batch)
        {
            printf("[llama_jni.cpp] Error: Invalid context or batch pointer in llama_decode JNI\n");
            fflush(stdout);
            return -1;
        }
        return llama_decode(ctx, *batch);
    }

    // --- Sampling --- REMOVED Custom JNI greedy sampling
    /*
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1sample_1token_1greedy)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jlong batch_ptr) {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        llama_batch* batch = get_batch_ptr(env, batch_ptr);
        if (!ctx || !batch) {
             printf("[llama_jni.cpp] Error: Invalid context or batch pointer in llama_sample_token_greedy JNI\n"); fflush(stdout);
            return -1;
        }
        if (batch->n_tokens == 0) {
            printf("[llama_jni.cpp] Error: Cannot sample with zero tokens in the batch\n"); fflush(stdout);
            return -1;
        }

        const llama_model* model = llama_get_model(ctx);
        if (!model) {
            printf("[llama_jni.cpp] Error: Could not get model from context for sampling\n"); fflush(stdout);
            return -1;
        }
        const llama_vocab* vocab = llama_model_get_vocab(model);
        if (!vocab) {
            printf("[llama_jni.cpp] Error: llama_model_get_vocab failed for sampling\n"); fflush(stdout);
            return -1;
        }
        auto n_vocab = llama_vocab_n_tokens(vocab);
        if (n_vocab <= 0) {
             printf("[llama_jni.cpp] Error: Invalid vocab size %d\n", n_vocab); fflush(stdout);
             return -1;
        }

        auto * logits = llama_get_logits_ith(ctx, batch->n_tokens - 1);
        if (!logits) {
            printf("[llama_jni.cpp] Error: Could not get logits for sampling (batch n_tokens: %d)\n", batch->n_tokens); fflush(stdout);
            return -1;
        }

        // --- Debugging Removed ---

        // Manual greedy sampling
        llama_token max_logit_token = 0;
        float max_logit = -std::numeric_limits<float>::infinity();
        for (llama_token token_id = 0; token_id < n_vocab; ++token_id) {
            if (logits[token_id] > max_logit) {
                max_logit = logits[token_id];
                max_logit_token = token_id;
            }
        }

        // --- Debugging Removed ---

        return static_cast<jint>(max_logit_token);
    }
    */

    // --- NEW JNI function to get logits ---
    JNIEXPORT jfloatArray JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1get_1logits_1ith)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jint index)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (!ctx)
        {
            printf("[llama_jni.cpp] Error: Invalid context pointer in llama_get_logits_ith JNI\n");
            fflush(stdout);
            return nullptr;
        }

        float *logits = llama_get_logits_ith(ctx, static_cast<int32_t>(index));
        if (!logits)
        {
            // This is not necessarily an error, it can happen if the index is invalid
            // printf("[llama_jni.cpp] Warning: llama_get_logits_ith returned null for index %d\n", index); fflush(stdout);
            return nullptr;
        }

        const llama_model *model = llama_get_model(ctx);
        if (!model)
        {
            printf("[llama_jni.cpp] Error: Could not get model from context in llama_get_logits_ith\n");
            fflush(stdout);
            return nullptr;
        }
        const llama_vocab *vocab = llama_model_get_vocab(model);
        if (!vocab)
        {
            printf("[llama_jni.cpp] Error: llama_model_get_vocab failed in llama_get_logits_ith\n");
            fflush(stdout);
            return nullptr;
        }
        int n_vocab = llama_vocab_n_tokens(vocab);
        if (n_vocab <= 0)
        {
            printf("[llama_jni.cpp] Error: Invalid vocab size %d in llama_get_logits_ith\n", n_vocab);
            fflush(stdout);
            return nullptr;
        }

        // Create a new Java float array
        jfloatArray result = env->NewFloatArray(n_vocab);
        if (result == nullptr)
        {
            printf("[llama_jni.cpp] Error: Failed to allocate NewFloatArray in llama_get_logits_ith\n");
            fflush(stdout);
            return nullptr; // Out of memory error
        }

        // Copy the C logits array to the Java float array
        env->SetFloatArrayRegion(result, 0, n_vocab, logits);

        return result;
    }

    // --- Detokenization --- Use llama_model_get_vocab
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1token_1to_1piece)(
        JNIEnv *env,
        jobject thiz,
        jlong model_ptr,
        jint token,
        jbyteArray buf_array,
        jint n_buf)
    {

        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model); // Use llama_model_get_vocab
        if (!vocab)
        {
            printf("[llama_jni.cpp] Error: llama_model_get_vocab failed for detokenization\n");
            fflush(stdout);
            return -3;
        }

        jbyte *buf_ptr = env->GetByteArrayElements(buf_array, nullptr);
        if (!buf_ptr)
            return -2;

        int result = llama_token_to_piece(
            vocab,
            static_cast<llama_token>(token),
            reinterpret_cast<char *>(buf_ptr),
            static_cast<size_t>(n_buf),
            0,    // lstrip = 0
            false // special
        );

        env->ReleaseByteArrayElements(buf_array, buf_ptr, 0);
        return static_cast<jint>(result);
    }

    // --- NEW Detokenization (llama_detokenize) ---
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1detokenize)(
        JNIEnv *env,
        jobject thiz,
        jlong model_ptr,
        jintArray tokens_array,
        jbyteArray text_buf_array,
        jint n_tokens,
        jint text_buf_size,
        jboolean remove_special,
        jboolean unparse_special)
    {

        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        if (!vocab)
        {
            printf("[llama_jni.cpp] Error: llama_model_get_vocab failed for detokenization\n");
            fflush(stdout);
            return -4;
        }

        jint *tokens_ptr = env->GetIntArrayElements(tokens_array, nullptr);
        if (!tokens_ptr)
            return -2;

        jbyte *text_buf_ptr = env->GetByteArrayElements(text_buf_array, nullptr);
        if (!text_buf_ptr)
        {
            env->ReleaseIntArrayElements(tokens_array, tokens_ptr, JNI_ABORT); // Abort as we didn't use tokens
            return -3;
        }

        int result = llama_detokenize(
            vocab,
            reinterpret_cast<const llama_token *>(tokens_ptr),
            static_cast<int32_t>(n_tokens),
            reinterpret_cast<char *>(text_buf_ptr),
            static_cast<int32_t>(text_buf_size),
            static_cast<bool>(remove_special),
            static_cast<bool>(unparse_special));

        env->ReleaseIntArrayElements(tokens_array, tokens_ptr, JNI_ABORT); // Read-only
        env->ReleaseByteArrayElements(text_buf_array, text_buf_ptr, 0);    // Copy back changes

        return static_cast<jint>(result);
    }

    // --- Special Tokens Implementations --- Use llama_model_get_vocab
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1token_1bos)(
        JNIEnv *env,
        jobject thiz,
        jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_bos(vocab) : -1;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1token_1eos)(
        JNIEnv *env,
        jobject thiz,
        jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_eos(vocab) : -1;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1token_1nl)(
        JNIEnv *env,
        jobject thiz,
        jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_nl(vocab) : -1;
    }

    // --- NEW Special Tokens ---
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1token_1pad)(
        JNIEnv *env,
        jobject thiz,
        jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_pad(vocab) : -1;
    }

    // --- KV Cache Management Implementations ---
    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1kv_1cache_1rm)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jint seq_id,
        jint p0,
        jint p1)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (ctx)
        {
            llama_kv_self_seq_rm(ctx, seq_id, p0, p1); // Use llama_kv_self_*
        }
    }

    // Mapped to llama_kv_cache_seq_n in NativeMethods.kt
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1kv_1cache_1seq_1n)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jint seq_id)
    {
        // Functionality removed - llama_kv_cache_seq_n does not exist in included llama.h
        printf("[llama_jni.cpp] WARN: JNI llama_kv_cache_seq_n called, but underlying function is missing! Returning dummy value.\n");
        fflush(stdout);
        return 0; // Dummy return
        /*
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (!ctx) return -1;
        // return llama_kv_cache_seq_n(ctx, seq_id);
        */
    }

    // --- NEW KV Cache Functions ---
    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1kv_1cache_1clear)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (ctx)
        {
            llama_kv_self_clear(ctx); // Use llama_kv_self_*
        }
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1kv_1cache_1seq_1cp)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jint seq_id_src,
        jint seq_id_dst,
        jint p0,
        jint p1)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (ctx)
        {
            llama_kv_self_seq_cp(ctx, seq_id_src, seq_id_dst, p0, p1); // Use llama_kv_self_*
        }
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1kv_1cache_1seq_1keep)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jint seq_id)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (ctx)
        {
            llama_kv_self_seq_keep(ctx, seq_id); // Use llama_kv_self_*
        }
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1kv_1cache_1seq_1shift)( // Renamed from llama_kv_cache_seq_add for clarity
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jint seq_id,
        jint p0,
        jint p1,
        jint delta)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (ctx)
        {
            llama_kv_self_seq_add(ctx, seq_id, p0, p1, delta); // Use llama_kv_self_seq_add
        }
    }

    // --- Tokenization --- Use llama_model_get_vocab
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1tokenize)(
        JNIEnv *env,
        jobject thiz,
        jlong model_ptr,
        jstring text,
        jintArray tokens_array,
        jint n_max_tokens,
        jboolean add_bos,
        jboolean special)
    {

        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model); // Use llama_model_get_vocab
        if (!vocab)
        {
            printf("[llama_jni.cpp] Error: llama_model_get_vocab failed for tokenization\n");
            fflush(stdout);
            return -4;
        }

        const char *text_cstr = env->GetStringUTFChars(text, nullptr);
        if (!text_cstr)
            return -2;

        jint *tokens_ptr = env->GetIntArrayElements(tokens_array, nullptr);
        if (!tokens_ptr)
        {
            env->ReleaseStringUTFChars(text, text_cstr);
            return -3;
        }

        int n_tokens = llama_tokenize(
            vocab,
            text_cstr,
            strlen(text_cstr), // Assuming null-terminated string from Java
            reinterpret_cast<llama_token *>(tokens_ptr),
            static_cast<int>(n_max_tokens),
            static_cast<bool>(add_bos), // Renamed from add_special in llama.h v1434
            static_cast<bool>(special)  // Renamed from parse_special in llama.h v1434
        );

        env->ReleaseIntArrayElements(tokens_array, tokens_ptr, 0); // Copy back changes
        env->ReleaseStringUTFChars(text, text_cstr);

        return static_cast<jint>(n_tokens);
    }

    // --- NEW: Get pooling type ---
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1get_1pooling_1type)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr)
    {
        const llama_context *ctx = reinterpret_cast<const llama_context *>(context_ptr);
        return ctx ? static_cast<jint>(llama_pooling_type(ctx)) : static_cast<jint>(LLAMA_POOLING_TYPE_UNSPECIFIED);
    }

    // --- NEW: Model loading from splits ---
    // NOTE: String array handling in JNI is complex. This version has potential memory leaks
    // with GetStringUTFChars if errors occur or if ReleaseStringUTFChars is not correctly paired.
    // A robust implementation might require global references or different string passing.
    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1load_1from_1splits)(
        JNIEnv *env,
        jobject thiz,
        jobjectArray paths_array,
        jint n_paths,
        jobject params)
    {
        if (n_paths <= 0 || !paths_array)
        {
            printf("[llama_jni.cpp] Error: Invalid paths array/count for split loading.\n");
            fflush(stdout);
            return 0;
        }

        std::vector<const char *> paths_cstr_ptrs(n_paths);
        std::vector<jstring> jstring_refs(n_paths); // Keep refs for releasing
        bool error = false;

        for (int i = 0; i < n_paths; ++i)
        {
            jstring path_jstr = (jstring)env->GetObjectArrayElement(paths_array, i);
            if (!path_jstr)
            {
                printf("[llama_jni.cpp] Error: Failed to get path string at index %d.\n", i);
                fflush(stdout);
                error = true;
                n_paths = i; // Only release up to this point
                break;
            }
            jstring_refs[i] = path_jstr; // Store ref
            paths_cstr_ptrs[i] = env->GetStringUTFChars(path_jstr, nullptr);
            if (!paths_cstr_ptrs[i])
            {
                printf("[llama_jni.cpp] Error: GetStringUTFChars failed for path at index %d.\n", i);
                fflush(stdout);
                error = true;
                n_paths = i + 1; // Release this one too
                break;
            }
        }

        jlong model_result = 0;
        if (!error)
        {
            llama_model_params model_params = model_params_from_java(env, params);
            llama_model *model = llama_model_load_from_splits(paths_cstr_ptrs.data(), n_paths, model_params);
            model_result = reinterpret_cast<jlong>(model);
        }

        // Release C strings and local jstring references
        for (int i = 0; i < n_paths; ++i)
        {
            if (paths_cstr_ptrs[i])
            {
                env->ReleaseStringUTFChars(jstring_refs[i], paths_cstr_ptrs[i]);
            }
            if (jstring_refs[i])
            {
                env->DeleteLocalRef(jstring_refs[i]);
            }
        }

        return model_result;
    }

    // --- NEW: System Info / Supports ---
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1max_1devices)(JNIEnv *env, jobject thiz)
    {
        return llama_max_devices();
    }

    JNIEXPORT jboolean JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1supports_1mmap)(JNIEnv *env, jobject thiz)
    {
        return llama_supports_mmap();
    }

    JNIEXPORT jboolean JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1supports_1mlock)(JNIEnv *env, jobject thiz)
    {
        return llama_supports_mlock();
    }

    JNIEXPORT jboolean JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1supports_1gpu_1offload)(JNIEnv *env, jobject thiz)
    {
        return llama_supports_gpu_offload();
    }

    // llama_supports_rpc - Omitted as likely less common for direct binding

    // --- NEW: Context Getters ---
    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1get_1kv_1cache_1self)(JNIEnv *env, jobject thiz, jlong context_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        return ctx ? reinterpret_cast<jlong>(llama_get_kv_self(ctx)) : 0;
    }

    // --- NEW: Model Getters ---
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1rope_1type)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        return model ? static_cast<jint>(llama_model_rope_type(model)) : static_cast<jint>(LLAMA_ROPE_TYPE_NONE);
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1n_1layer)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        return model ? llama_model_n_layer(model) : 0;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1n_1head)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        return model ? llama_model_n_head(model) : 0;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1n_1head_1kv)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        return model ? llama_model_n_head_kv(model) : 0;
    }

    JNIEXPORT jfloat JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1rope_1freq_1scale_1train)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        return model ? llama_model_rope_freq_scale_train(model) : 0.0f;
    }

    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1size)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        return model ? llama_model_size(model) : 0;
    }

    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1n_1params)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        return model ? llama_model_n_params(model) : 0;
    }

    JNIEXPORT jboolean JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1has_1encoder)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        return model ? llama_model_has_encoder(model) : false;
    }

    JNIEXPORT jboolean JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1has_1decoder)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        return model ? llama_model_has_decoder(model) : false;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1decoder_1start_1token)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        return model ? llama_model_decoder_start_token(model) : -1;
    }

    JNIEXPORT jboolean JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1model_1is_1recurrent)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        return model ? llama_model_is_recurrent(model) : false;
    }

    // --- NEW: Vocab Getters ---
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1type)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return static_cast<jint>(LLAMA_VOCAB_TYPE_NONE);
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? static_cast<jint>(llama_vocab_type(vocab)) : static_cast<jint>(LLAMA_VOCAB_TYPE_NONE);
    }

    JNIEXPORT jstring JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1get_1text)(JNIEnv *env, jobject thiz, jlong model_ptr, jint token)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return env->NewStringUTF("");
        const llama_vocab *vocab = llama_model_get_vocab(model);
        if (!vocab)
            return env->NewStringUTF("");
        const char *text = llama_vocab_get_text(vocab, token);
        return text ? env->NewStringUTF(text) : env->NewStringUTF("");
    }

    JNIEXPORT jfloat JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1get_1score)(JNIEnv *env, jobject thiz, jlong model_ptr, jint token)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return 0.0f;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_get_score(vocab, token) : 0.0f;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1get_1attr)(JNIEnv *env, jobject thiz, jlong model_ptr, jint token)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return static_cast<jint>(LLAMA_TOKEN_ATTR_UNDEFINED);
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? static_cast<jint>(llama_vocab_get_attr(vocab, token)) : static_cast<jint>(LLAMA_TOKEN_ATTR_UNDEFINED);
    }

    JNIEXPORT jboolean JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1is_1eog)(JNIEnv *env, jobject thiz, jlong model_ptr, jint token)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return false;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_is_eog(vocab, token) : false;
    }

    JNIEXPORT jboolean JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1is_1control)(JNIEnv *env, jobject thiz, jlong model_ptr, jint token)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return false;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_is_control(vocab, token) : false;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1eot)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_eot(vocab) : -1;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1sep)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_sep(vocab) : -1;
    }

    JNIEXPORT jboolean JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1get_1add_1bos)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return false;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_get_add_bos(vocab) : false;
    }

    JNIEXPORT jboolean JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1get_1add_1eos)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return false;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_get_add_eos(vocab) : false;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1fim_1pre)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_fim_pre(vocab) : -1;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1fim_1suf)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_fim_suf(vocab) : -1;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1fim_1mid)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_fim_mid(vocab) : -1;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1fim_1pad)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_fim_pad(vocab) : -1;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1fim_1rep)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_fim_rep(vocab) : -1;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1vocab_1fim_1sep)(JNIEnv *env, jobject thiz, jlong model_ptr)
    {
        const llama_model *model = reinterpret_cast<const llama_model *>(model_ptr);
        if (!model)
            return -1;
        const llama_vocab *vocab = llama_model_get_vocab(model);
        return vocab ? llama_vocab_fim_sep(vocab) : -1;
    }

    // --- NEW: State / Session Management ---
    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1state_1get_1size)(JNIEnv *env, jobject thiz, jlong context_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        return ctx ? llama_state_get_size(ctx) : 0;
    }

    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1state_1get_1data)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jbyteArray dst_array,
        jlong capacity)
    { // capacity is size_t
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (!ctx || !dst_array || capacity <= 0)
            return 0;

        // Ensure capacity does not exceed array length
        jsize array_len = env->GetArrayLength(dst_array);
        size_t actual_capacity = static_cast<size_t>(capacity);
        if (array_len < capacity)
        {
            printf("[llama_jni.cpp] Warning: llama_state_get_data capacity (%lld) > dst_array length (%d). Truncating.\n", capacity, array_len);
            actual_capacity = array_len;
        }

        jbyte *dst_ptr = env->GetByteArrayElements(dst_array, nullptr);
        if (!dst_ptr)
            return 0;

        size_t bytes_copied = llama_state_get_data(ctx, reinterpret_cast<uint8_t *>(dst_ptr), actual_capacity);

        env->ReleaseByteArrayElements(dst_array, dst_ptr, (bytes_copied > 0) ? 0 : JNI_ABORT); // Copy back if copied > 0

        return static_cast<jlong>(bytes_copied);
    }

    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1state_1set_1data)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jbyteArray src_array,
        jlong size)
    { // size is size_t
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (!ctx || !src_array || size <= 0)
            return 0;

        // Check if array length is sufficient
        jsize array_len = env->GetArrayLength(src_array);
        if (array_len < size)
        {
            printf("[llama_jni.cpp] Error: llama_state_set_data src_array length (%d) < provided size (%lld)\n", array_len, size);
            fflush(stdout);
            return 0;
        }

        jbyte *src_ptr = env->GetByteArrayElements(src_array, nullptr);
        if (!src_ptr)
            return 0;

        size_t bytes_read = llama_state_set_data(ctx, reinterpret_cast<const uint8_t *>(src_ptr), static_cast<size_t>(size));

        env->ReleaseByteArrayElements(src_array, src_ptr, JNI_ABORT); // Source data is read-only

        return static_cast<jlong>(bytes_read);
    }

    // Returns token count, or -1 on failure.
    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1state_1load_1file)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jstring path_session,
        jintArray tokens_out_array,
        jint n_token_capacity)
    {

        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (!ctx || !tokens_out_array || n_token_capacity <= 0)
            return -1L;

        // Check capacity vs array length
        jsize array_len = env->GetArrayLength(tokens_out_array);
        size_t actual_capacity = static_cast<size_t>(n_token_capacity);
        if (array_len < n_token_capacity)
        {
            printf("[llama_jni.cpp] Warning: llama_state_load_file capacity (%d) > tokens_out_array length (%d). Truncating.\n", n_token_capacity, array_len);
            actual_capacity = array_len;
        }

        const char *path_cstr = env->GetStringUTFChars(path_session, nullptr);
        if (!path_cstr)
            return -1L;

        jint *tokens_out_ptr = env->GetIntArrayElements(tokens_out_array, nullptr);
        if (!tokens_out_ptr)
        {
            env->ReleaseStringUTFChars(path_session, path_cstr);
            return -1L;
        }

        size_t n_token_count_out_c = 0;
        bool success_c = llama_state_load_file(
            ctx,
            path_cstr,
            reinterpret_cast<llama_token *>(tokens_out_ptr),
            actual_capacity,
            &n_token_count_out_c);

        env->ReleaseStringUTFChars(path_session, path_cstr);
        // Copy back tokens if successful
        env->ReleaseIntArrayElements(tokens_out_array, tokens_out_ptr, success_c ? 0 : JNI_ABORT);

        return success_c ? static_cast<jlong>(n_token_count_out_c) : -1L;
    }

    JNIEXPORT jboolean JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1state_1save_1file)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jstring path_session,
        jintArray tokens_array,
        jlong n_token_count)
    { // size_t

        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (!ctx || !tokens_array || n_token_count < 0)
            return false;

        const char *path_cstr = env->GetStringUTFChars(path_session, nullptr);
        if (!path_cstr)
            return false;

        jint *tokens_ptr = env->GetIntArrayElements(tokens_array, nullptr);
        if (!tokens_ptr)
        {
            env->ReleaseStringUTFChars(path_session, path_cstr);
            return false;
        }
        // Check array length >= n_token_count
        jsize array_len = env->GetArrayLength(tokens_array);
        if (array_len < n_token_count)
        {
            printf("[llama_jni.cpp] Error: llama_state_save_file tokens_array length (%d) < provided count (%lld)\n", array_len, n_token_count);
            fflush(stdout);
            env->ReleaseStringUTFChars(path_session, path_cstr);
            env->ReleaseIntArrayElements(tokens_array, tokens_ptr, JNI_ABORT);
            return false;
        }

        bool success_c = llama_state_save_file(
            ctx,
            path_cstr,
            reinterpret_cast<const llama_token *>(tokens_ptr),
            static_cast<size_t>(n_token_count));

        env->ReleaseStringUTFChars(path_session, path_cstr);
        env->ReleaseIntArrayElements(tokens_array, tokens_ptr, JNI_ABORT); // Read-only

        return success_c;
    }

    // --- NEW: Sequence State Management (Single Sequence) ---
    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1state_1seq_1get_1size)(
        JNIEnv *env, jobject thiz, jlong context_ptr, jint seq_id)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        return ctx ? llama_state_seq_get_size(ctx, seq_id) : 0;
    }

    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1state_1seq_1get_1data)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jbyteArray dst_array,
        jlong capacity, // size_t
        jint seq_id)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (!ctx || !dst_array || capacity <= 0)
            return 0;

        jsize array_len = env->GetArrayLength(dst_array);
        size_t actual_capacity = static_cast<size_t>(capacity);
        if (array_len < capacity)
        {
            printf("[llama_jni.cpp] Warning: llama_state_seq_get_data capacity (%lld) > dst_array length (%d). Truncating.\n", capacity, array_len);
            actual_capacity = array_len;
        }

        jbyte *dst_ptr = env->GetByteArrayElements(dst_array, nullptr);
        if (!dst_ptr)
            return 0;

        size_t bytes_copied = llama_state_seq_get_data(ctx, reinterpret_cast<uint8_t *>(dst_ptr), actual_capacity, seq_id);

        env->ReleaseByteArrayElements(dst_array, dst_ptr, (bytes_copied > 0) ? 0 : JNI_ABORT);

        return static_cast<jlong>(bytes_copied);
    }

    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1state_1seq_1set_1data)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jbyteArray src_array,
        jlong size, // size_t
        jint dest_seq_id)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (!ctx || !src_array || size <= 0)
            return 0;

        jsize array_len = env->GetArrayLength(src_array);
        if (array_len < size)
        {
            printf("[llama_jni.cpp] Error: llama_state_seq_set_data src_array length (%d) < provided size (%lld)\n", array_len, size);
            fflush(stdout);
            return 0;
        }

        jbyte *src_ptr = env->GetByteArrayElements(src_array, nullptr);
        if (!src_ptr)
            return 0;

        size_t bytes_read = llama_state_seq_set_data(ctx, reinterpret_cast<const uint8_t *>(src_ptr), static_cast<size_t>(size), dest_seq_id);

        env->ReleaseByteArrayElements(src_array, src_ptr, JNI_ABORT);

        return static_cast<jlong>(bytes_read);
    }

    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1state_1seq_1save_1file)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jstring filepath,
        jint seq_id,
        jintArray tokens_array,
        jlong n_token_count)
    { // size_t

        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (!ctx || !tokens_array || n_token_count < 0)
            return 0;

        const char *filepath_cstr = env->GetStringUTFChars(filepath, nullptr);
        if (!filepath_cstr)
            return 0;

        jint *tokens_ptr = env->GetIntArrayElements(tokens_array, nullptr);
        if (!tokens_ptr)
        {
            env->ReleaseStringUTFChars(filepath, filepath_cstr);
            return 0;
        }
        jsize array_len = env->GetArrayLength(tokens_array);
        if (array_len < n_token_count)
        {
            printf("[llama_jni.cpp] Error: llama_state_seq_save_file tokens_array length (%d) < provided count (%lld)\n", array_len, n_token_count);
            fflush(stdout);
            env->ReleaseStringUTFChars(filepath, filepath_cstr);
            env->ReleaseIntArrayElements(tokens_array, tokens_ptr, JNI_ABORT);
            return 0;
        }

        size_t bytes_written = llama_state_seq_save_file(
            ctx,
            filepath_cstr,
            seq_id,
            reinterpret_cast<const llama_token *>(tokens_ptr),
            static_cast<size_t>(n_token_count));

        env->ReleaseStringUTFChars(filepath, filepath_cstr);
        env->ReleaseIntArrayElements(tokens_array, tokens_ptr, JNI_ABORT);

        return static_cast<jlong>(bytes_written);
    }

    JNIEXPORT jlong JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1state_1seq_1load_1file)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jstring filepath,
        jint dest_seq_id,
        jintArray tokens_out_array,
        jint n_token_capacity)
    {

        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (!ctx || !tokens_out_array || n_token_capacity <= 0)
            return -1L;

        jsize array_len = env->GetArrayLength(tokens_out_array);
        size_t actual_capacity = static_cast<size_t>(n_token_capacity);
        if (array_len < n_token_capacity)
        {
            printf("[llama_jni.cpp] Warning: llama_state_seq_load_file capacity (%d) > tokens_out_array length (%d). Truncating.\n", n_token_capacity, array_len);
            actual_capacity = array_len;
        }

        const char *filepath_cstr = env->GetStringUTFChars(filepath, nullptr);
        if (!filepath_cstr)
            return -1L;

        jint *tokens_out_ptr = env->GetIntArrayElements(tokens_out_array, nullptr);
        if (!tokens_out_ptr)
        {
            env->ReleaseStringUTFChars(filepath, filepath_cstr);
            return -1L;
        }

        size_t n_token_count_out_c = 0;
        size_t bytes_read = llama_state_seq_load_file(
            ctx,
            filepath_cstr,
            dest_seq_id,
            reinterpret_cast<llama_token *>(tokens_out_ptr),
            actual_capacity,
            &n_token_count_out_c);

        env->ReleaseStringUTFChars(filepath, filepath_cstr);
        // Copy back tokens if successful (bytes_read > 0 implies success)
        env->ReleaseIntArrayElements(tokens_out_array, tokens_out_ptr, (bytes_read > 0) ? 0 : JNI_ABORT);

        return (bytes_read > 0) ? static_cast<jlong>(n_token_count_out_c) : -1L;
    }

    // --- NEW: Encoding ---
    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1encode)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jlong batch_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        llama_batch *batch = get_batch_ptr(env, batch_ptr);
        if (!ctx || !batch)
        {
            printf("[llama_jni.cpp] Error: Invalid context or batch pointer in llama_encode JNI\n");
            fflush(stdout);
            return -1;
        }
        // Need to dereference the batch pointer when passing to llama_encode
        return llama_encode(ctx, *batch);
    }

    // --- NEW: Threading and Misc Settings ---
    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1set_1n_1threads)(
        JNIEnv *env,
        jobject thiz,
        jlong context_ptr,
        jint n_threads,
        jint n_threads_batch)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (ctx)
        {
            llama_set_n_threads(ctx, n_threads, n_threads_batch);
        }
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1n_1threads)(JNIEnv *env, jobject thiz, jlong context_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        return ctx ? llama_n_threads(ctx) : 0;
    }

    JNIEXPORT jint JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1n_1threads_1batch)(JNIEnv *env, jobject thiz, jlong context_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        return ctx ? llama_n_threads_batch(ctx) : 0;
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1set_1embeddings)(JNIEnv *env, jobject thiz, jlong context_ptr, jboolean embeddings)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (ctx)
        {
            llama_set_embeddings(ctx, embeddings);
        }
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1set_1causal_1attn)(JNIEnv *env, jobject thiz, jlong context_ptr, jboolean causal_attn)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (ctx)
        {
            llama_set_causal_attn(ctx, causal_attn);
        }
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1set_1warmup)(JNIEnv *env, jobject thiz, jlong context_ptr, jboolean warmup)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (ctx)
        {
            llama_set_warmup(ctx, warmup);
        }
    }

    JNIEXPORT void JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1synchronize)(JNIEnv *env, jobject thiz, jlong context_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (ctx)
        {
            llama_synchronize(ctx);
        }
    }

    // --- NEW: Getting Embeddings ---
    JNIEXPORT jfloatArray JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1get_1embeddings)(JNIEnv *env, jobject thiz, jlong context_ptr)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (!ctx)
            return nullptr;

        float *embeddings = llama_get_embeddings(ctx);
        if (!embeddings)
            return nullptr;

        // Calculating the total size (n_outputs * n_embd) is complex via JNI
        // as n_outputs depends on the last submitted batch configuration.
        // Returning null. Use llama_get_embeddings_ith or llama_get_embeddings_seq instead.
        printf("[llama_jni.cpp] WARN: llama_get_embeddings() JNI wrapper returns null due to complexity. Use _ith or _seq variants.\n");
        fflush(stdout);
        return nullptr;

        /*
        // Previous attempt - Size calculation requires more context
        const llama_model *model = llama_get_model(ctx);
        if (!model) return nullptr;
        int n_embd = llama_model_n_embd(model);
        if (n_embd <= 0) return nullptr;
        // int n_outputs = ???; // Need to determine this
        // size_t total_size = static_cast<size_t>(n_outputs) * n_embd;
        // jfloatArray result = env->NewFloatArray(total_size);
        // if (!result) return nullptr;
        // env->SetFloatArrayRegion(result, 0, total_size, embeddings);
        // return result;
        */
    }

    JNIEXPORT jfloatArray JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1get_1embeddings_1ith)(JNIEnv *env, jobject thiz, jlong context_ptr, jint index)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (!ctx)
            return nullptr;

        float *embedding = llama_get_embeddings_ith(ctx, index);
        if (!embedding)
            return nullptr;

        const llama_model *model = llama_get_model(ctx);
        if (!model)
            return nullptr;
        int n_embd = llama_model_n_embd(model);
        if (n_embd <= 0)
            return nullptr;

        jfloatArray result = env->NewFloatArray(n_embd);
        if (!result)
            return nullptr;

        env->SetFloatArrayRegion(result, 0, n_embd, embedding);
        return result;
    }

    JNIEXPORT jfloatArray JNICALL
    JNI_METHOD_NAME(LLAMA_JNI_PREFIX, llama_1get_1embeddings_1seq)(JNIEnv *env, jobject thiz, jlong context_ptr, jint seq_id)
    {
        llama_context *ctx = reinterpret_cast<llama_context *>(context_ptr);
        if (!ctx)
            return nullptr;

        float *embedding = llama_get_embeddings_seq(ctx, seq_id);
        if (!embedding)
            return nullptr;

        const llama_model *model = llama_get_model(ctx);
        if (!model)
            return nullptr;
        int n_embd = llama_model_n_embd(model);
        if (n_embd <= 0)
            return nullptr;

        enum llama_pooling_type p_type = llama_pooling_type(ctx);
        // If pooling type is NONE, llama_get_embeddings_seq returns NULL, handled above.
        // If pooling type is RANK, returns float[1]. Otherwise float[n_embd].
        int result_size = (p_type == LLAMA_POOLING_TYPE_RANK) ? 1 : n_embd;

        jfloatArray result = env->NewFloatArray(result_size);
        if (!result)
            return nullptr;

        env->SetFloatArrayRegion(result, 0, result_size, embedding);
        return result;
    }
}
