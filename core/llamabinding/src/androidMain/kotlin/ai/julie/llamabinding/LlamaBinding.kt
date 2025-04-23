package ai.julie.llamabinding

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class LlamaBinding actual constructor(
    /**
     * The absolute path to the .gguf model file on the Android device's filesystem.
     * IMPORTANT: The calling code (e.g., in the :composeApp module) is responsible for:
     * 1. Determining the correct path (e.g., using Context.getFilesDir()).
     * 2. Ensuring the model file exists at this path (e.g., by copying from assets
     *    or downloading it) *before* creating an instance of LlamaBinding.
     */
    modelPath: String
) {
    private var modelPtr: Long = 0
    private var ctxPtr: Long = 0

    init {
        // Ensure backend is initialized (idempotent)
        LlamaPlatform.initializeBackend()

        // Create default parameter instances (using defaults from NativeMethods.kt)
        val defaultModelParams = LlamaModelParams()
        val defaultContextParams = LlamaContextParams()

        // Use Android Logcat for logging
        logMessage("Attempting to load model from provided path: $modelPath")
        if (modelPath.isBlank() || modelPath == "/path/to/model/on/android/device.gguf") {
             logMessage("Warning: Invalid or placeholder model path provided: '$modelPath'. Ensure the caller provides a valid path.", isError = true)
             // Optionally throw an exception here if path is clearly invalid
             // throw IllegalArgumentException("Invalid model path provided: $modelPath")
        }

        modelPtr = NativeMethods.llama_model_load_from_file(modelPath, defaultModelParams)
        if (modelPtr == 0L) {
            logMessage("Failed to load Llama model from path: $modelPath", isError = true)
            // Consider throwing a more specific exception if needed
            throw RuntimeException("Failed to load Llama model from path: $modelPath. Check if file exists and is valid.")
        }
        logMessage("Model loaded successfully (pointer: $modelPtr).")

        logMessage("Initializing context with default params...")
        ctxPtr = NativeMethods.llama_context_init_from_model(modelPtr, defaultContextParams)
        if (ctxPtr == 0L) {
            logMessage("Failed to initialize Llama context.", isError = true)
            // Ensure model is freed if context fails
            NativeMethods.llama_model_free(modelPtr)
            modelPtr = 0 
            throw RuntimeException("Failed to initialize Llama context.")
        }
        logMessage("Context initialized successfully (pointer: $ctxPtr).")
    }

    actual fun close() {
        if (ctxPtr != 0L) {
            logMessage("Freeing context (pointer: $ctxPtr)...")
            NativeMethods.llama_context_free(ctxPtr)
            ctxPtr = 0
            logMessage("Context freed.")
        }
        if (modelPtr != 0L) {
            logMessage("Freeing model (pointer: $modelPtr)...")
            NativeMethods.llama_model_free(modelPtr)
            modelPtr = 0
            logMessage("Model freed.")
        }
    }

    actual fun predict(prompt: String): String {
        if (ctxPtr == 0L) throw IllegalStateException("Llama context is not initialized or has been closed.")
        if (modelPtr == 0L) throw IllegalStateException("Llama model is not initialized or has been closed.")

        // --- Explicit KV Cache Cleanup for sequence 0 --- 
        val sequenceIdToClear = 0
        NativeMethods.llama_kv_cache_rm(ctxPtr, sequenceIdToClear, -1, -1) // Remove all tokens for seq 0
        logMessage("Explicitly cleared KV cache for sequence ID: $sequenceIdToClear")
        // ---------------------------------------------

        val nCtx = NativeMethods.llama_n_ctx(ctxPtr)
        // Leave some buffer space. Adjust as needed.
        val maxContextSize = nCtx - 4
        // Max tokens to generate in one predict call
        val maxTokensToGenerate = maxContextSize

        // --- Tokenize the prompt --- 
        val bosToken = NativeMethods.llama_token_bos(modelPtr)
        val eosToken = NativeMethods.llama_token_eos(modelPtr)

        if (eosToken == -1) { // Still need EOS for stopping
             logMessage("Failed to get EOS token from model", isError = true)
             throw RuntimeException("Failed to get EOS token from model")
        }

        // Tokenize prompt securely
        val promptTokens = IntArray(maxContextSize) 
        val nPromptTokens = NativeMethods.llama_tokenize(
            model = modelPtr,
            text = prompt,
            tokens = promptTokens,
            n_max_tokens = maxContextSize,
            add_bos = true, // Let tokenizer add BOS if needed
            special = true
        )

        if (nPromptTokens < 0) {
            logMessage("Failed to tokenize prompt (code: $nPromptTokens): $prompt", isError = true)
            throw RuntimeException("Failed to tokenize prompt (code: $nPromptTokens): $prompt")
        }
        val tokensToProcess = promptTokens.take(nPromptTokens)

        // --- Generation Loop ---
        val stringBuilder = StringBuilder()
        var nEval = 0 // Number of tokens evaluated from prompt + generation
        var nGen = 0 // Number of tokens generated so far
        val sequenceId = intArrayOf(0) // Use sequence ID 0 for this simple case

        // Initialize batch
        val batchPtr = NativeMethods.llama_batch_init(
            n_tokens = NativeMethods.llama_n_ctx(ctxPtr), // Max batch size can be nCtx
            embd = 0,           // We are not using embeddings input
            n_seq_max = 1       // Only generating one sequence
        )
        if (batchPtr == 0L) {
            logMessage("Failed to initialize llama_batch", isError = true)
            throw RuntimeException("Failed to initialize llama_batch")
        }

        try {
            // Evaluate the initial prompt tokens
            logMessage("Evaluating initial prompt tokens...")
            NativeMethods.llama_batch_clear(batchPtr)

            // Manually populate the batch using JNI helpers
            for (i in tokensToProcess.indices) {
                 NativeMethods.llama_batch_set_token(batchPtr, i, tokensToProcess[i])
                 NativeMethods.llama_batch_set_pos(batchPtr, i, i)
                 NativeMethods.llama_batch_set_seq_id(batchPtr, i, sequenceId[0]) // Assuming sequence 0
                 NativeMethods.llama_batch_set_logits(batchPtr, i, (i == tokensToProcess.size - 1))
            }
            NativeMethods.llama_batch_set_n_tokens(batchPtr, tokensToProcess.size)
            logMessage("Batch populated for prompt evaluation. n_tokens = ${tokensToProcess.size}")

            // Decode the prompt batch
            val promptDecodeResult = NativeMethods.llama_decode(ctxPtr, batchPtr)
            if (promptDecodeResult != 0) {
                logMessage("llama_decode failed during prompt evaluation (code: $promptDecodeResult) - Batch likely empty/incorrect!", isError = true)
                throw RuntimeException("llama_decode failed during prompt evaluation (code: $promptDecodeResult) - Batch likely empty/incorrect!")
            }
            nEval = tokensToProcess.size
            logMessage("Prompt evaluation complete. Next step needs sampling result.")

            // Generation loop
            while (nGen < maxTokensToGenerate) {
                // --- Sample Next Token ---
                val logitIndexInLastBatch = if (nGen == 0) nEval - 1 else 0
                val logits = NativeMethods.llama_get_logits_ith(ctxPtr, logitIndexInLastBatch)
                if (logits == null) {
                    logMessage("Error: Could not get logits (index: $logitIndexInLastBatch). Stopping generation.", isError = true)
                    break
                }

                // Greedy sampling in Kotlin
                var maxLogit = -Float.MAX_VALUE
                var newTokenId = -1
                for (tokenId in logits.indices) {
                    if (logits[tokenId] > maxLogit) {
                        maxLogit = logits[tokenId]
                        newTokenId = tokenId
                    }
                }

                if (newTokenId == -1) {
                    logMessage("Error: Failed to find max logit. Stopping generation.", isError = true)
                    break // Should not happen
                }
                logMessage("Sampled token: $newTokenId (Logit: $maxLogit)")

                // Check for EOS
                if (newTokenId == eosToken || nEval >= maxContextSize) {
                    logMessage("EOS token or max length reached. Stopping generation.")
                    break
                }

                // --- Detokenize and Append ---
                val pieceBuffer = ByteArray(32)
                val nBytes = NativeMethods.llama_token_to_piece(modelPtr, newTokenId, pieceBuffer, pieceBuffer.size)
                if (nBytes < 0) {
                    logMessage("Error: llama_token_to_piece failed (code: $nBytes) for token $newTokenId", isError = true)
                    break
                } else if (nBytes > 0) {
                    val piece = pieceBuffer.copyOfRange(0, nBytes).toString(Charsets.UTF_8)
                    stringBuilder.append(piece)
                    // TODO: Consider alternative to print/flush for Android (e.g., update UI state)
                }

                nGen++

                // --- Prepare Batch for Next Token ---
                NativeMethods.llama_batch_clear(batchPtr)
                NativeMethods.llama_batch_set_token(batchPtr, 0, newTokenId)
                NativeMethods.llama_batch_set_pos(batchPtr, 0, nEval)
                NativeMethods.llama_batch_set_seq_id(batchPtr, 0, sequenceId[0])
                NativeMethods.llama_batch_set_logits(batchPtr, 0, true)
                NativeMethods.llama_batch_set_n_tokens(batchPtr, 1)
                logMessage("Batch populated for generation step. n_tokens = 1, pos = $nEval")

                // --- Evaluate the New Token ---
                val decodeResult = NativeMethods.llama_decode(ctxPtr, batchPtr)
                if (decodeResult != 0) {
                    logMessage("Error: llama_decode failed during generation (code: $decodeResult) - Batch likely empty/incorrect!", isError = true)
                    break
                }
                nEval++
            }
        } finally {
            if (batchPtr != 0L) {
                NativeMethods.llama_batch_free(batchPtr)
                logMessage("llama_batch freed.")
            }
            // TODO: Remove println() if not needed for Android UI updates
        }

        return stringBuilder.toString()
    }

    actual fun getContextSize(): Int {
        if (ctxPtr == 0L) throw IllegalStateException("Llama context is not initialized or has been closed.")
        return NativeMethods.llama_n_ctx(ctxPtr)
    }

    actual fun getVocabSize(): Int {
        if (ctxPtr == 0L) throw IllegalStateException("Llama context is not initialized or has been closed.")
        if (modelPtr == 0L) throw IllegalStateException("Llama model is not initialized or has been closed.")
        return NativeMethods.llama_model_n_vocab(modelPtr)
    }

    actual fun getEmbeddingSize(): Int {
        if (modelPtr == 0L) throw IllegalStateException("Llama model is not initialized or has been closed.")
        return NativeMethods.llama_model_n_embd(modelPtr)
    }

    actual fun getModelDescription(): String {
        if (modelPtr == 0L) throw IllegalStateException("Llama model is not initialized or has been closed.")
        val bufferSize = 256
        val buffer = ByteArray(bufferSize)
        val length = NativeMethods.llama_model_desc(modelPtr, buffer, bufferSize)
        return if (length > 0 && length < bufferSize) {
            buffer.decodeToString(0, length)
        } else if (length >= bufferSize) {
            buffer.decodeToString() + "... (truncated)"
        } else {
            logMessage("Failed to get model description", isError = true)
            "(Failed to get model description)"
        }
    }

    // Private helper for logging using Android Logcat
    // TODO: Add dependency on Android core library if not already present
    private fun logMessage(message: String, isError: Boolean = false) {
        val tag = "LlamaBinding"
        if (isError) {
            android.util.Log.e(tag, message)
        } else {
            android.util.Log.i(tag, message)
        }
    }
} 