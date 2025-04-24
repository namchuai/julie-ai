package ai.julie.llamabinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Note: LlamaBinding and LlamaPlatform are already in this package

// Create a scope for launching background tasks if needed
// TODO: Consider using Android-specific scope (e.g., viewModelScope) if applicable
private val bindingUtilScope = CoroutineScope(Dispatchers.Default)

/**
 * Actual Android implementation for triggering the Llama model test load.
 * Uses the LlamaBinding abstraction from this module.
 *
 * IMPORTANT: This test function ASSUMES the model file has ALREADY been copied
 * from assets to the app's internal storage by other application code
 * (e.g., in the Application class or MainActivity).
 */
actual fun triggerTestLlamaLoad() {
    // NOTE: LlamaPlatform.initializeBackend() should be called ONCE at app startup.
    // NOTE: LlamaPlatform.freeBackend() should be called ONCE at app shutdown.
    // We assume they are called elsewhere (e.g., in the main Android Application class).

    // Define the expected model name and location in internal storage
    val targetModelName = "tinyllama-1.1b-chat-v1.0.Q2_K.gguf"
    // We cannot get Context here directly, so we construct the path relative to
    // where context.filesDir would be. This is fragile and relies on the app
    // using the same targetName when copying the asset.
    val expectedInternalPath = "/data/data/ai.julie/files/models/$targetModelName" // Hardcoded package name - VERY FRAGILE
    // A better approach would involve passing the path obtained using Context into this function,
    // or having a shared component provide the path.
    logMessage("TriggerTestLlamaLoad assuming model exists at: $expectedInternalPath")

    // NOTE: This test will FAIL if the file hasn't been copied to internal storage first!

    val modelPath = expectedInternalPath // Use the expected internal path

    logMessage("Attempting to create LlamaBinding for test: $modelPath")

    bindingUtilScope.launch {
        // TODO: Consider Dispatchers.IO for file access, but check Android best practices
        withContext(Dispatchers.IO) {
            var binding: LlamaBinding? = null
            try {
                logMessage("Creating LlamaBinding instance...")
                // Pass the expected internal path to the constructor
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
                logMessage("Exception during LlamaBinding test: ${e.message}", isError = true)
                // Log stack trace using Android Logcat
                android.util.Log.e("LlamaBindingUtil", "Exception in Llama test", e)
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
 * Actual Android implementation for clearing Llama test logs.
 */
actual fun clearTestLlamaLog() {
    logMessage("[Log Cleared]")
    // If we had Android-specific state for logs, clear it here
}

// Simple logging function for Android using Logcat
// TODO: Add dependency on Android core library if not already present
private fun logMessage(message: String, isError: Boolean = false) {
    val tag = "LlamaBindingUtil"
    if (isError) {
        android.util.Log.e(tag, message)
    } else {
        android.util.Log.i(tag, message)
    }
} 