package ai.julie.llamabinding

import ai.julie.core.model.LlamaSamplerSettings
import ai.julie.core.model.ModelContextParams
import ai.julie.core.model.ModelLoadParams
import ai.julie.llamabinding.model.LlamaContextParams
import ai.julie.llamabinding.model.LlamaModelParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.math.exp
import kotlin.random.Random

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class LlamaBinding {
    private var modelPtr: Long = 0
    private var ctxPtr: Long = 0

    actual suspend fun initialize() {
        // Initialize the LLAMA backend
        LlamaPlatform.initializeBackend()
        println("LlamaBinding initialized successfully.")
    }

    /**
     * Load a model with progress callback support
     */
    actual suspend fun loadModel(
        modelPath: String,
        modelLoadParams: ModelLoadParams,
        modelContextParams: ModelContextParams,
        progressCallback: LlamaProgressCallback?
    ) {
        // Create parameter instances from the provided parameters
        val llamaModelParams = LlamaModelParams.from(modelLoadParams)
        val llamaContextParams = LlamaContextParams.from(modelContextParams)

        println("Loading model from: $modelPath with provided params...")
        println("ModelContextParams: nCtx=${modelContextParams.nCtx}, nBatch=${modelContextParams.nBatch}, nUbatch=${modelContextParams.nUbatch}")
        println("LlamaContextParams: nCtx=${llamaContextParams.nCtx}, nBatch=${llamaContextParams.nBatch}, nUbatch=${llamaContextParams.nUbatch}")
        modelPtr = NativeMethods.llama_model_load_from_file_with_progress(
            modelPath,
            llamaModelParams,
            progressCallback
        )

        require(modelPtr != 0L) { "Failed to load Llama model from path: $modelPath" }

        println("Model loaded successfully (pointer: $modelPtr).")

        println("Initializing context with provided params...")
        ctxPtr = NativeMethods.llama_context_init_from_model(modelPtr, llamaContextParams)
        require(ctxPtr != 0L) {
            NativeMethods.llama_model_free(modelPtr)
            modelPtr = 0
            "Failed to initialize Llama context with model pointer: $modelPtr"
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

    actual fun predict(
        prompt: String,
        samplerSettings: LlamaSamplerSettings
    ): Flow<String> = flow {
        if (ctxPtr == 0L) throw LlamaContextException.ContextNotInitializedException()
        if (modelPtr == 0L) throw LlamaContextException.ModelNotLoadedException()

        // Get model's default stop tokens and combine with user-provided ones
        val modelStopTokens = getModelStopTokens()
        logMessage("Model stop tokens detected: $modelStopTokens")
        logMessage("User stop tokens from settings: ${samplerSettings.stop}")
        val allStopWords = modelStopTokens + samplerSettings.stop
        logMessage("Combined stop words being used: $allStopWords")

        // --- Explicit KV Cache Cleanup for sequence 0 --- 
        val sequenceIdToClear = 0
        NativeMethods.llama_kv_cache_rm(
            ctxPtr,
            sequenceIdToClear,
            -1,
            -1
        ) // Remove all tokens for seq 0
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

        // Log prompt details for debugging
        logMessage("Prompt length: ${prompt.length} characters")
        logMessage("Max context size: $maxContextSize tokens")

        // Tokenize prompt securely
        val promptTokens = IntArray(maxContextSize)
        // Check if prompt already starts with BOS token to avoid duplicates
        // TODO: this is quite bad code. FYI, <s> is for llama1/2 and <|begin_of_text|> is for llama3. This should be added based on the model architecture?
        val shouldAddBos = !prompt.startsWith("<|begin_of_text|>") && !prompt.startsWith("<s>")
        logMessage("Adding BOS token: $shouldAddBos")

        val nPromptTokens = NativeMethods.llama_tokenize(
            model = modelPtr,
            text = prompt,
            tokens = promptTokens,
            n_max_tokens = maxContextSize,
            add_bos = shouldAddBos, // Only add BOS if prompt doesn't already have it
            special = true
        )

        logMessage("Tokenization result: $nPromptTokens tokens")
        if (nPromptTokens < 0) {
            logMessage("Tokenization failed with code: $nPromptTokens")
            logMessage("Full prompt that failed: $prompt")
            throw LlamaContextException.TokenizationFailedException(
                errorCode = nPromptTokens,
                promptLength = prompt.length
            )
        }

        if (nPromptTokens >= maxContextSize) {
            throw LlamaContextException.ContextTooSmallException(
                promptTokens = nPromptTokens,
                maxContextTokens = maxContextSize,
                promptLength = prompt.length
            )
        }
        // Use the tokens directly from the tokenizer output
        val tokensToProcess = promptTokens.take(nPromptTokens)

        // --- Generation Loop ---
        // val stringBuilder = StringBuilder() // Not needed when emitting directly
        var nEval = 0 // Number of tokens evaluated from prompt + generation
        var nGen = 0 // Number of tokens generated so far
        val sequenceId = intArrayOf(0) // Use sequence ID 0 for this simple case
        val generatedText = StringBuilder() // Track generated text for stop word checking

        // Initialize batch
        val batchPtr = NativeMethods.llama_batch_init(
            n_tokens = NativeMethods.llama_n_ctx(ctxPtr), // Max batch size can be nCtx
            embd = 0,           // We are not using embeddings input
            n_seq_max = 1       // Only generating one sequence
        )
        require(batchPtr != 0L) { "Failed to initialize llama_batch" }

        try {
            // Evaluate the initial prompt tokens
            logMessage("Evaluating initial prompt tokens...")
            NativeMethods.llama_batch_clear(batchPtr)

            // Manually populate the batch using JNI helpers
            for (i in tokensToProcess.indices) { // CHANGED: Use tokensToProcess
                NativeMethods.llama_batch_set_token(batchPtr, i, tokensToProcess[i]) // CHANGED
                NativeMethods.llama_batch_set_pos(batchPtr, i, i)
                NativeMethods.llama_batch_set_seq_id(
                    batchPtr,
                    i,
                    sequenceId[0]
                ) // Assuming sequence 0
                // Only need logits for the last token of the prompt
                NativeMethods.llama_batch_set_logits(
                    batchPtr,
                    i,
                    (i == tokensToProcess.size - 1)
                ) // CHANGED
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

                // 2. Sample next token using sampler settings
                val newTokenId = sampleToken(logits, samplerSettings)

                if (newTokenId == -1) {
                    logMessage("Error: Failed to sample token. Stopping generation.")
                    break
                }
                logMessage("Sampled token: $newTokenId using temperature: ${samplerSettings.temperature}")

                // Check for EOS
                if (newTokenId == eosToken || nEval >= maxContextSize) {
                    logMessage("EOS token or max length reached. Stopping generation.")
                    break
                }

                // --- Detokenize and Append --- (This part should still work)
                val pieceBuffer =
                    ByteArray(32) // INCREASED BUFFER SIZE: Max size for one token piece
                val nBytes = NativeMethods.llama_token_to_piece(
                    modelPtr,
                    newTokenId,
                    pieceBuffer,
                    pieceBuffer.size
                )
                if (nBytes < 0) {
                    logMessage("Error: llama_token_to_piece failed (code: $nBytes) for token $newTokenId")
                    // Optionally break or continue based on desired error handling
                    break
                } else if (nBytes > 0) {
                    val piece = pieceBuffer.copyOfRange(0, nBytes).toString(Charsets.UTF_8)
                    generatedText.append(piece)

                    // Check for stop words (model defaults + user provided)
                    val currentText = generatedText.toString()
                    var shouldStop = false
                    for (stopWord in allStopWords) {
                        if (stopWord.isNotEmpty() && currentText.contains(stopWord)) {
                            logMessage("Stop word found: '$stopWord' in generated text")
                            logMessage("Current generated text: '${currentText.takeLast(100)}'") // Show last 100 chars
                            shouldStop = true
                            break
                        }
                    }

                    // Also log the token piece for debugging
                    logMessage("Generated token piece: '$piece'")

                    emit(piece) // Emit the generated piece

                    if (shouldStop) break
                    // print(piece) // Removed, consumer will handle display
                    // System.out.flush() // Removed
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
                NativeMethods.llama_batch_set_pos(
                    batchPtr,
                    0,
                    nEval
                )         // Position is the total number evaluated so far
                NativeMethods.llama_batch_set_seq_id(
                    batchPtr,
                    0,
                    sequenceId[0]
                ) // Assuming sequence 0
                NativeMethods.llama_batch_set_logits(
                    batchPtr,
                    0,
                    true
                )       // Need logits for the newly generated token to sample the *next* one
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
            // println() // Ensure newline after generation - Removed, consumer handles display
        }

        // return stringBuilder.toString() // Not needed when emitting
    }.flowOn(Dispatchers.Default)

    actual fun getContextSize(): Int {
        if (ctxPtr == 0L) throw LlamaContextException.ContextNotInitializedException()
        return NativeMethods.llama_n_ctx(ctxPtr)
    }

    actual fun getVocabSize(): Int {
        if (ctxPtr == 0L) throw LlamaContextException.ContextNotInitializedException()
        if (modelPtr == 0L) throw LlamaContextException.ModelNotLoadedException()
        return NativeMethods.llama_model_n_vocab(modelPtr)
    }

    actual fun getEmbeddingSize(): Int {
        if (modelPtr == 0L) throw LlamaContextException.ModelNotLoadedException()
        return NativeMethods.llama_model_n_embd(modelPtr)
    }

    // Direct llama.cpp function wrappers - minimal logic, pragmatic names
    
    actual fun tokenize(text: String, maxTokens: Int, addBos: Boolean): IntArray {
        if (modelPtr == 0L) throw LlamaContextException.ModelNotLoadedException()
        
        val tokens = IntArray(maxTokens)
        val nTokens = NativeMethods.llama_tokenize(
            model = modelPtr,
            text = text,
            tokens = tokens,
            n_max_tokens = maxTokens,
            add_bos = addBos,
            special = true
        )
        
        if (nTokens < 0) throw LlamaContextException.TokenizationFailedException(nTokens, text.length)
        return tokens.copyOf(nTokens)
    }
    
    actual fun createBatch(maxTokens: Int): Long {
        return NativeMethods.llama_batch_init(maxTokens, 0, 1)
    }
    
    actual fun freeBatch(batch: Long) {
        NativeMethods.llama_batch_free(batch)
    }
    
    actual fun clearBatch(batch: Long) {
        NativeMethods.llama_batch_clear(batch)
    }
    
    actual fun setBatchToken(batch: Long, index: Int, tokenId: Int) {
        NativeMethods.llama_batch_set_token(batch, index, tokenId)
    }
    
    actual fun setBatchPosition(batch: Long, index: Int, position: Int) {
        NativeMethods.llama_batch_set_pos(batch, index, position)
    }
    
    actual fun setBatchSequenceId(batch: Long, index: Int, seqId: Int) {
        NativeMethods.llama_batch_set_seq_id(batch, index, seqId)
    }
    
    actual fun setBatchLogits(batch: Long, index: Int, needLogits: Boolean) {
        NativeMethods.llama_batch_set_logits(batch, index, needLogits)
    }
    
    actual fun setBatchSize(batch: Long, nTokens: Int) {
        NativeMethods.llama_batch_set_n_tokens(batch, nTokens)
    }
    
    actual fun decode(batch: Long): Int {
        if (ctxPtr == 0L) throw LlamaContextException.ContextNotInitializedException()
        return NativeMethods.llama_decode(ctxPtr, batch)
    }
    
    actual fun getLogits(index: Int): FloatArray? {
        if (ctxPtr == 0L) throw LlamaContextException.ContextNotInitializedException()
        return NativeMethods.llama_get_logits_ith(ctxPtr, index)
    }
    
    actual fun tokenToText(tokenId: Int): String {
        if (modelPtr == 0L) throw LlamaContextException.ModelNotLoadedException()
        
        val buffer = ByteArray(32)
        val nBytes = NativeMethods.llama_token_to_piece(modelPtr, tokenId, buffer, buffer.size)
        
        return if (nBytes > 0) {
            buffer.copyOfRange(0, nBytes).toString(Charsets.UTF_8)
        } else {
            ""
        }
    }
    
    actual fun getBosToken(): Int {
        if (modelPtr == 0L) throw LlamaContextException.ModelNotLoadedException()
        return NativeMethods.llama_token_bos(modelPtr)
    }
    
    actual fun getEosToken(): Int {
        if (modelPtr == 0L) throw LlamaContextException.ModelNotLoadedException()
        return NativeMethods.llama_token_eos(modelPtr)
    }
    
    actual fun clearKvCache(seqId: Int, start: Int, end: Int) {
        if (ctxPtr == 0L) throw LlamaContextException.ContextNotInitializedException()
        NativeMethods.llama_kv_cache_rm(ctxPtr, seqId, start, end)
    }
    
    actual fun sampleNextToken(logits: FloatArray, samplerSettings: LlamaSamplerSettings): Int {
        return sampleToken(logits, samplerSettings) // Use existing implementation
    }

    actual fun getModelDescription(): String {
        if (modelPtr == 0L) throw LlamaContextException.ModelNotLoadedException()
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

    actual suspend fun recreateContext(newContextParams: ModelContextParams) {
        if (modelPtr == 0L) throw LlamaContextException.ModelNotLoadedException()

        println("Recreating context with new parameters...")
        println("New params: nCtx=${newContextParams.nCtx}, nBatch=${newContextParams.nBatch}, nUbatch=${newContextParams.nUbatch}")

        // Free existing context if it exists
        if (ctxPtr != 0L) {
            println("Freeing existing context (pointer: $ctxPtr)...")
            NativeMethods.llama_context_free(ctxPtr)
            ctxPtr = 0
            println("Existing context freed.")
        }

        // Create new context with updated parameters
        val llamaContextParams = LlamaContextParams.from(newContextParams)
        println("Creating new context with updated params...")
        ctxPtr = NativeMethods.llama_context_init_from_model(modelPtr, llamaContextParams)

        require(ctxPtr != 0L) {
            "Failed to recreate Llama context with new parameters"
        }

        println("Context recreated successfully (pointer: $ctxPtr).")
        println("New context size: ${NativeMethods.llama_n_ctx(ctxPtr)} tokens")
    }

    // Private helper for logging within this class
    private fun logMessage(message: String) {
        println("[LlamaBinding] $message")
    }

    /**
     * Sample a token from logits using the provided sampler settings
     */
    private fun sampleToken(logits: FloatArray, samplerSettings: LlamaSamplerSettings): Int {
        return when {
            samplerSettings.temperature <= 0.0f -> {
                // Greedy sampling (temperature = 0)
                greedySample(logits)
            }

            else -> {
                // Temperature-based sampling
                temperatureSample(logits, samplerSettings)
            }
        }
    }

    /**
     * Greedy sampling - pick the token with highest logit
     */
    private fun greedySample(logits: FloatArray): Int {
        var maxLogit = -Float.MAX_VALUE
        var bestToken = -1

        for (tokenId in logits.indices) {
            if (logits[tokenId] > maxLogit) {
                maxLogit = logits[tokenId]
                bestToken = tokenId
            }
        }

        return bestToken
    }

    /**
     * Temperature-based sampling with top-k and top-p support
     */
    private fun temperatureSample(logits: FloatArray, samplerSettings: LlamaSamplerSettings): Int {
        // Step 1: Apply temperature
        val scaledLogits = logits.map { it / samplerSettings.temperature }.toFloatArray()

        // Step 2: Convert to probabilities using softmax
        val probabilities = softmax(scaledLogits)

        // Step 3: Apply top-k filtering if enabled
        val filteredProbs =
            if (samplerSettings.topK > 0 && samplerSettings.topK < probabilities.size) {
                applyTopK(probabilities, samplerSettings.topK)
            } else {
                probabilities
            }

        // Step 4: Apply top-p (nucleus) filtering if enabled
        val finalProbs = if (samplerSettings.topP < 1.0f) {
            applyTopP(filteredProbs, samplerSettings.topP)
        } else {
            filteredProbs
        }

        // Step 5: Sample from the filtered distribution
        return sampleFromProbabilities(finalProbs, samplerSettings.seed)
    }

    /**
     * Convert logits to probabilities using softmax
     */
    private fun softmax(logits: FloatArray): FloatArray {
        // Find max for numerical stability
        val maxLogit = logits.maxOrNull() ?: 0f

        // Calculate exp values
        val expValues = logits.map { exp(it - maxLogit) }
        val sum = expValues.sum()

        // Normalize to probabilities
        return expValues.map { it / sum }.toFloatArray()
    }

    /**
     * Apply top-k filtering - keep only the top k tokens
     */
    private fun applyTopK(probabilities: FloatArray, topK: Int): FloatArray {
        // Create token-probability pairs and sort by probability (descending)
        val tokenProbs = probabilities.mapIndexed { index, prob -> index to prob }
            .sortedByDescending { it.second }

        // Zero out probabilities for tokens not in top-k
        val filtered = FloatArray(probabilities.size) { 0f }

        for (i in 0 until minOf(topK, tokenProbs.size)) {
            val (tokenIndex, prob) = tokenProbs[i]
            filtered[tokenIndex] = prob
        }

        // Renormalize
        val sum = filtered.sum()
        return if (sum > 0) {
            filtered.map { it / sum }.toFloatArray()
        } else {
            probabilities // Fallback to original if filtering failed
        }
    }

    /**
     * Apply top-p (nucleus) filtering - keep tokens until cumulative probability exceeds p
     */
    private fun applyTopP(probabilities: FloatArray, topP: Float): FloatArray {
        // Create token-probability pairs and sort by probability (descending)
        val tokenProbs = probabilities.mapIndexed { index, prob -> index to prob }
            .sortedByDescending { it.second }

        val filtered = FloatArray(probabilities.size) { 0f }
        var cumulativeProb = 0f

        // Add tokens until we exceed top-p threshold
        for ((tokenIndex, prob) in tokenProbs) {
            if (cumulativeProb >= topP) break

            filtered[tokenIndex] = prob
            cumulativeProb += prob
        }

        // Renormalize
        val sum = filtered.sum()
        return if (sum > 0) {
            filtered.map { it / sum }.toFloatArray()
        } else {
            probabilities // Fallback to original if filtering failed
        }
    }

    /**
     * Sample from probability distribution
     */
    private fun sampleFromProbabilities(probabilities: FloatArray, seed: Int): Int {
        // Use provided seed or random
        val random = if (seed >= 0) Random(seed) else Random.Default
        val randomValue = random.nextFloat()

        var cumulativeProb = 0f
        for (i in probabilities.indices) {
            cumulativeProb += probabilities[i]
            if (randomValue < cumulativeProb) {
                return i
            }
        }

        // Fallback to last token if we didn't sample anything (shouldn't happen)
        return probabilities.size - 1
    }

    /**
     * Get model's default stop tokens from GGUF metadata
     */
    private fun getModelStopTokens(): List<String> {
        if (modelPtr == 0L) return emptyList()

        val stopTokens = mutableListOf<String>()

        try {
            // Get EOS token ID and convert to string
            val eosTokenId = NativeMethods.llama_token_eos(modelPtr)
            if (eosTokenId >= 0) {
                val eosTokenString = NativeMethods.llama_model_token_to_piece(modelPtr, eosTokenId)
                if (!eosTokenString.isNullOrEmpty()) {
                    stopTokens.add(eosTokenString)
                    logMessage("Added model EOS token: '$eosTokenString' (ID: $eosTokenId)")
                }
            }

            // Try to get chat template stop tokens if available
            val chatTemplate =
                NativeMethods.llama_model_meta_val_str(modelPtr, "tokenizer.chat_template")
            if (!chatTemplate.isNullOrEmpty()) {
                // Look for common stop sequences in chat templates
                val commonStops = listOf("</s>", "<|eot_id|>", "<|end_of_text|>", "<|im_end|>")
                for (stop in commonStops) {
                    if (chatTemplate.contains(stop) && !stopTokens.contains(stop)) {
                        stopTokens.add(stop)
                        logMessage("Added chat template stop token: '$stop'")
                    }
                }
            }

        } catch (e: Exception) {
            logMessage("Error reading model stop tokens: ${e.message}")
        }

        return stopTokens
    }
}
