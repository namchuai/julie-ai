package ai.julie.llamabinding

// Contains pointer typealiases, concrete param classes, and the NativeMethods object.

// Represents the opaque pointers returned by llama.cpp
typealias LlamaModelPointer = Long
typealias LlamaContextPointer = Long
typealias LlamaBatchPointer = Long // Assuming we wrap llama_batch in the future

// Represents a llama token ID
typealias llama_token = Int

// Concrete parameter class for Desktop/JNI (not expect/actual)
// Defaults based on llama.cpp C++ defaults.
data class LlamaModelParams(
    val nGpuLayers: Int = 0,
    val mainGpu: Int = 0,
    val vocabOnly: Boolean = false,
    val useMmap: Boolean = true,
    val useMlock: Boolean = false,
    val checkTensors: Boolean = false
)

// Concrete parameter class for Desktop/JNI (not expect/actual)
// Defaults based on llama.cpp C++ defaults.
data class LlamaContextParams(
    val nCtx: Int = 512,
    val nBatch: Int = 2048, // Default from C++
    val nUbatch: Int = 512,
    val nSeqMax: Int = 1,
    val nThreads: Int = 0, // Default 0 allows auto-detection by library
    val nThreadsBatch: Int = 0, // Default 0 allows auto-detection by library
    val ropeFreqBase: Float = 0.0f,
    val ropeFreqScale: Float = 0.0f,
    val embeddings: Boolean = false,
    val offloadKqv: Boolean = true,
    val flashAttn: Boolean = false,
    val noPerf: Boolean = true // Default from C++
)

/**
 * Object containing the native JNI method declarations.
 */
object NativeMethods {

    private var libraryLoaded = false

    init {
        synchronized(this) {
            if (!libraryLoaded) {
                try {
                    println("Attempting to load native library 'llama_jni'...")
                    System.loadLibrary("llama_jni")
                    libraryLoaded = true
                    println("Native library 'llama_jni' loaded successfully.")
                } catch (e: UnsatisfiedLinkError) {
                    System.err.println("FATAL: Failed to load native library 'llama_jni'.")
                    System.err.println("Ensure the :llamacpp module built successfully and the library was copied.")
                    System.err.println("Check java.library.path.")
                    System.err.println("Original error: ${e.message}")
                    throw e
                }
            }
        }
    }

    // --- Backend Initialization ---
    external fun llama_backend_init()
    external fun llama_backend_free()

    // --- Model Loading ---
    // Uses the concrete LlamaModelParams defined above
    external fun llama_model_load_from_file(path: String, params: LlamaModelParams): LlamaModelPointer
    external fun llama_model_free(model: LlamaModelPointer)

    // --- Context Management ---
    // Uses the concrete LlamaContextParams defined above
    external fun llama_context_init_from_model(model: LlamaModelPointer, params: LlamaContextParams): LlamaContextPointer
    external fun llama_context_free(context: LlamaContextPointer)

    // --- Context Info ---
    external fun llama_n_ctx(context: LlamaContextPointer): Int
    external fun llama_get_model(context: LlamaContextPointer): LlamaModelPointer

    // --- Model Info ---
    external fun llama_model_n_vocab(model: LlamaModelPointer): Int
    external fun llama_model_n_ctx_train(model: LlamaModelPointer): Int
    external fun llama_model_n_embd(model: LlamaModelPointer): Int
    external fun llama_model_desc(model: LlamaModelPointer, buffer: ByteArray, bufferSize: Int): Int

    // --- Tokenization --- NEW
    external fun llama_tokenize(
        model: LlamaModelPointer,
        text: String,
        tokens: IntArray, // llama_token* output
        n_max_tokens: Int,
        add_bos: Boolean,
        special: Boolean
    ): Int

    // --- Decoding/Evaluation --- NEW
    // Using llama_decode is preferred for efficiency but requires llama_batch management.
    // Placeholder for llama_eval (less efficient for generation but simpler JNI initially):
    // external fun llama_eval(ctx: LlamaContextPointer, tokens: IntArray, n_tokens: Int, n_past: Int): Int
    // Proper implementation requires JNI bindings for llama_batch_* functions
    external fun llama_decode(ctx: LlamaContextPointer, batch: LlamaBatchPointer): Int

    // --- Sampling --- NEW
    // Logits are retrieved via JNI, sampling logic (e.g., greedy search) is done in Kotlin.
    external fun llama_get_logits_ith(ctx: LlamaContextPointer, i: Int): FloatArray?
    // Example for temperature sampling (would also require logits + Kotlin logic)

    // --- Detokenization --- NEW
    external fun llama_token_to_piece(
        model: LlamaModelPointer,
        token: llama_token,
        buf: ByteArray,
        n_buf: Int
    ): Int

    // --- KV Cache Management --- NEW
    external fun llama_kv_cache_rm(ctx: LlamaContextPointer, seq_id: Int, p0: Int, p1: Int)
    // external fun llama_kv_cache_seq_n(ctx: LlamaContextPointer, seq_id: Int): Int // Commented out - Missing in llama.h

    // --- Batch Management (Requires complex JNI struct mapping) --- NEW
    external fun llama_batch_init(n_tokens: Int, embd: Int, n_seq_max: Int): LlamaBatchPointer
    external fun llama_batch_free(batch: LlamaBatchPointer)
    external fun llama_batch_clear(batch: LlamaBatchPointer)
    // external fun llama_batch_add(batch: LlamaBatchPointer, token: llama_token, pos: Int, seq_ids: IntArray, logits: Boolean) // Commented out - Missing in llama.h
    // Note: Manual batch manipulation would require new JNI helpers

    // --- Manual Batch Manipulation Helpers (use if llama_batch_add missing) --- NEW
    external fun llama_batch_set_n_tokens(batch: LlamaBatchPointer, n_tokens: Int)
    external fun llama_batch_set_token(batch: LlamaBatchPointer, index: Int, token: llama_token)
    external fun llama_batch_set_pos(batch: LlamaBatchPointer, index: Int, pos: Int)
    external fun llama_batch_set_seq_id(batch: LlamaBatchPointer, index: Int, seq_id: Int)
    external fun llama_batch_set_logits(batch: LlamaBatchPointer, index: Int, logits: Boolean)
    // --- End Manual Batch Helpers ---

    // --- Special Tokens --- NEW
    external fun llama_token_bos(model: LlamaModelPointer): llama_token
    external fun llama_token_eos(model: LlamaModelPointer): llama_token
    external fun llama_token_nl(model: LlamaModelPointer): llama_token

    // TODO: Add external fun declarations for tokenization, evaluation, sampling etc.
    // e.g.:
    // external fun llama_tokenize(model: LlamaModelPointer, text: String, tokens: IntArray, maxTokens: Int, addBos: Boolean): Int
    // external fun llama_decode(context: LlamaContextPointer, batch: LlamaBatchPointer): Int // Assuming LlamaBatch is implemented
    // external fun llama_sample_token_greedy(context: LlamaContextPointer, candidates: LlamaTokenDataArray): Int // Assuming LlamaTokenDataArray is implemented
    // external fun llama_token_to_piece(model: LlamaModelPointer, token: Int, buf: ByteArray, bufSize: Int): Int
} 