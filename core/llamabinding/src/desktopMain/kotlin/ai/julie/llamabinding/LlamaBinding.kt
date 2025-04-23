package ai.julie.llamabinding

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class LlamaBinding actual constructor(
    modelPath: String // Only model path is needed from common code
) {
    private var modelPtr: Long = 0
    private var ctxPtr: Long = 0

    init {
        LlamaPlatform.initializeBackend()

        // Create default parameter instances (using defaults from NativeMethods.kt)
        val defaultModelParams = LlamaModelParams()
        val defaultContextParams = LlamaContextParams()

        println("Loading model from: $modelPath with default params...")
        modelPtr = NativeMethods.llama_model_load_from_file(modelPath, defaultModelParams)
        if (modelPtr == 0L) {
            throw RuntimeException("Failed to load Llama model from path: $modelPath")
        }
        println("Model loaded successfully (pointer: $modelPtr).")

        println("Initializing context with default params...")
        ctxPtr = NativeMethods.llama_context_init_from_model(modelPtr, defaultContextParams)
        if (ctxPtr == 0L) {
            NativeMethods.llama_model_free(modelPtr)
            modelPtr = 0
            throw RuntimeException("Failed to initialize Llama context.")
        }
        println("Context initialized successfully (pointer: $ctxPtr).")
    }

    actual fun close() {
        if (ctxPtr != 0L) {
            println("Freeing context (pointer: $ctxPtr)...")
            NativeMethods.llama_context_free(ctxPtr)
            ctxPtr = 0
            println("Context freed.")
        }
        if (modelPtr != 0L) {
            println("Freeing model (pointer: $modelPtr)...")
            NativeMethods.llama_model_free(modelPtr)
            modelPtr = 0
            println("Model freed.")
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
        // Let llama_tokenize handle BOS token addition based on model metadata
        // val tokensList = mutableListOf<llama_token>() // Not needed if tokenizer handles BOS
        val bosToken = NativeMethods.llama_token_bos(modelPtr)
        val eosToken = NativeMethods.llama_token_eos(modelPtr)

        if (eosToken == -1) { // Still need EOS for stopping
             throw RuntimeException("Failed to get EOS token from model")
        }

        // tokensList.add(bosToken) // REMOVED: Let llama_tokenize handle BOS

        // Tokenize prompt securely
        val promptTokens = IntArray(maxContextSize) 
        val nPromptTokens = NativeMethods.llama_tokenize(
            model = modelPtr,
            text = prompt, // REMOVED: Manual leading space
            tokens = promptTokens,
            n_max_tokens = maxContextSize,
            add_bos = true, // CHANGED: Let tokenizer add BOS if needed
            special = true
        )

        if (nPromptTokens < 0) {
            throw RuntimeException("Failed to tokenize prompt (code: $nPromptTokens): $prompt")
        }
        // Use the tokens directly from the tokenizer output
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
            throw RuntimeException("Failed to initialize llama_batch")
        }

        try {
            // Evaluate the initial prompt tokens
            logMessage("Evaluating initial prompt tokens...")
            NativeMethods.llama_batch_clear(batchPtr)

            // Manually populate the batch using JNI helpers
            for (i in tokensToProcess.indices) { // CHANGED: Use tokensToProcess
                 NativeMethods.llama_batch_set_token(batchPtr, i, tokensToProcess[i]) // CHANGED
                 NativeMethods.llama_batch_set_pos(batchPtr, i, i)
                 NativeMethods.llama_batch_set_seq_id(batchPtr, i, sequenceId[0]) // Assuming sequence 0
                 // Only need logits for the last token of the prompt
                 NativeMethods.llama_batch_set_logits(batchPtr, i, (i == tokensToProcess.size - 1)) // CHANGED
            }
            // Set the number of tokens in the batch *after* populating
            NativeMethods.llama_batch_set_n_tokens(batchPtr, tokensToProcess.size) // CHANGED
            logMessage("Batch populated for prompt evaluation. n_tokens = ${tokensToProcess.size}") // CHANGED

            // Decode the prompt batch
            val promptDecodeResult = NativeMethods.llama_decode(ctxPtr, batchPtr)
            if (promptDecodeResult != 0) {
                throw RuntimeException("llama_decode failed during prompt evaluation (code: $promptDecodeResult) - Batch likely empty/incorrect!")
            }
            nEval = tokensToProcess.size // CHANGED: Use size of actually processed tokens
            logMessage("Prompt evaluation complete. Next step needs sampling result.")

            // Generation loop
            while (nGen < maxTokensToGenerate) {
                // --- Sample Next Token ---
                // 1. Get logits from the last evaluated token in the *previous* batch
                // After the prompt decode, the relevant index was (nEval - 1)
                // Inside the loop, after decoding a batch of size 1, the relevant index is 0
                val logitIndexInLastBatch = if (nGen == 0) nEval - 1 else 0
                val logits = NativeMethods.llama_get_logits_ith(ctxPtr, logitIndexInLastBatch)
                if (logits == null) {
                    logMessage("Error: Could not get logits (index: $logitIndexInLastBatch). Stopping generation.")
                    break
                }

                // 2. Find the token with the highest logit (greedy sampling in Kotlin)
                var maxLogit = -Float.MAX_VALUE
                var newTokenId = -1
                for (tokenId in logits.indices) {
                    if (logits[tokenId] > maxLogit) {
                        maxLogit = logits[tokenId]
                        newTokenId = tokenId
                    }
                }

                if (newTokenId == -1) {
                    logMessage("Error: Failed to find max logit. Stopping generation.")
                    break // Should not happen if logits is not null
                }
                logMessage("Sampled token: $newTokenId (Logit: $maxLogit)")

                // Check for EOS
                if (newTokenId == eosToken || nEval >= maxContextSize) {
                    logMessage("EOS token or max length reached. Stopping generation.")
                    break
                }

                // --- Detokenize and Append --- (This part should still work)
                val pieceBuffer = ByteArray(32) // INCREASED BUFFER SIZE: Max size for one token piece
                val nBytes = NativeMethods.llama_token_to_piece(modelPtr, newTokenId, pieceBuffer, pieceBuffer.size)
                if (nBytes < 0) {
                    logMessage("Error: llama_token_to_piece failed (code: $nBytes) for token $newTokenId")
                    // Optionally break or continue based on desired error handling
                    break
                } else if (nBytes > 0) {
                    val piece = pieceBuffer.copyOfRange(0, nBytes).toString(Charsets.UTF_8)
                    stringBuilder.append(piece)
                    print(piece) // Print piece by piece
                    System.out.flush()
                }

                // Add the new token to the list for the next iteration's evaluation
                // We need a mutable list to track generated tokens for detokenization display
                // But the core logic now uses the JNI-returned logits based on nEval position
                // tokensList.add(newTokenId) // This list isn't directly used for batching anymore
                nGen++

                // --- Prepare Batch for Next Token ---
                NativeMethods.llama_batch_clear(batchPtr)

                // Manually populate the batch for the single new token
                NativeMethods.llama_batch_set_token(batchPtr, 0, newTokenId)
                NativeMethods.llama_batch_set_pos(batchPtr, 0, nEval)         // Position is the total number evaluated so far
                NativeMethods.llama_batch_set_seq_id(batchPtr, 0, sequenceId[0]) // Assuming sequence 0
                NativeMethods.llama_batch_set_logits(batchPtr, 0, true)       // Need logits for the newly generated token to sample the *next* one
                // Set the number of tokens in the batch *after* populating
                NativeMethods.llama_batch_set_n_tokens(batchPtr, 1)
                logMessage("Batch populated for generation step. n_tokens = 1, pos = $nEval")

                // --- Evaluate the New Token ---
                val decodeResult = NativeMethods.llama_decode(ctxPtr, batchPtr)
                if (decodeResult != 0) {
                    logMessage("Error: llama_decode failed during generation (code: $decodeResult) - Batch likely empty/incorrect!")
                    break
                }
                nEval++ // Increment evaluated token count

                // --- Context Management (Very Basic) ---
                // In a real scenario, may need KV cache shifting (llama_kv_cache_rm etc.)
                // if nEval exceeds nCtx. This simple example assumes nCtx is large enough.
            }
        } finally {
            if (batchPtr != 0L) {
                NativeMethods.llama_batch_free(batchPtr)
                logMessage("llama_batch freed.")
            }
            println() // Ensure newline after generation
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
            "(Failed to get model description)"
        }
    }

    // Private helper for logging within this class
    private fun logMessage(message: String) {
        println("[LlamaBinding] $message")
    }
} 