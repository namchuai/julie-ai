package ai.julie.llamabinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Note: LlamaBinding and LlamaPlatform are already in this package

// Create a scope for launching background tasks if needed
// This could potentially be moved or made internal if only used here.
private val bindingUtilScope = CoroutineScope(Dispatchers.Default)

/**
 * Actual desktop implementation for triggering the Llama model test load.
 * Uses the LlamaBinding abstraction from this module.
 */
actual fun triggerTestLlamaLoad() {
    // NOTE: LlamaPlatform.initializeBackend() should be called ONCE at app startup.
    // NOTE: LlamaPlatform.freeBackend() should be called ONCE at app shutdown.
    // We assume they are called elsewhere (e.g., in the main function).

    // Consider making the model path configurable or passed as an argument
    val modelPath = "/Users/jamesnguyen/Downloads/tinyllama-1.1b-chat-v1.0.Q4_K_S.gguf"

    logMessage("Attempting to create LlamaBinding for: $modelPath")

    bindingUtilScope.launch {
        withContext(Dispatchers.IO) { // Loading can still block
            var binding: LlamaBinding? = null
            try {
                logMessage("Creating LlamaBinding instance...")
                binding = LlamaBinding(modelPath)
                logMessage("LlamaBinding created successfully!")

                // Get info using the binding's methods
                val nVocab = binding.getVocabSize()
                val nCtx = binding.getContextSize()
                val nEmbd = binding.getEmbeddingSize()
                val description = binding.getModelDescription()

                logMessage("  - Vocab size: $nVocab")
                logMessage("  - Context size: $nCtx")
                logMessage("  - Embedding dims: $nEmbd")
                logMessage("  - Description: $description")

                // Perform a test prediction
                logMessage("Performing test prediction...")
                val prompt = "Explain Kotlin Multiplatform in one sentence."
                logMessage("Prompt: '$prompt'")
                val result = binding.predict(prompt) // Call predict
                logMessage("Prediction result: $result")

            } catch (e: Throwable) {
                logMessage("Exception during LlamaBinding test: ${e.message}")
                e.printStackTrace() // Log stack trace to console
            } finally {
                // --- Cleanup --- Use the binding's close method
                binding?.let {
                    logMessage("Closing LlamaBinding...")
                    it.close()
                    logMessage("LlamaBinding closed.")
                }
            }
        }
    }
}

/**
 * Actual desktop implementation for clearing Llama test logs (currently just prints).
 */
actual fun clearTestLlamaLog() {
    logMessage("[Log Cleared]")
    // If we had desktop-specific state for logs, clear it here
}

// Simple logging function for desktop (could be internal or moved)
private fun logMessage(message: String) {
    println("[Llama Binding Util] $message") // Changed log prefix
} 