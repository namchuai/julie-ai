package ai.julie.llamabinding

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Actual Android implementation for LlamaPlatform.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object LlamaPlatform {
    private val backendInitialized = AtomicBoolean(false)
    private val backendFreed = AtomicBoolean(false)

    // Ensure NativeMethods class (and its init block) is loaded when this object is accessed.
    // This should trigger System.loadLibrary("llama_jni") on Android.
    init {
        NativeMethods // Reference the object to ensure its init block runs
    }

    /**
     * Initializes the Llama backend via JNI.
     * Ensures it's called only once.
     */
    actual fun initializeBackend() {
        if (!backendInitialized.getAndSet(true)) {
            if (backendFreed.get()) {
                // This should ideally not happen if usage is correct (free only on app exit)
                logMessage("Warning: Llama backend initialized after being freed.", isError = true)
                backendFreed.set(false) // Reset freed state if re-initializing
            }
            logMessage("Initializing Llama backend...")
            NativeMethods.llama_backend_init()
            logMessage("Llama backend initialized.")
        } else {
            logMessage("Llama backend already initialized.")
        }
    }

    /**
     * Frees the Llama backend via JNI.
     * Ensures it's called only once and after initialization.
     */
    actual fun freeBackend() {
        if (backendInitialized.get() && !backendFreed.getAndSet(true)) {
            logMessage("Freeing Llama backend...")
            NativeMethods.llama_backend_free()
            backendInitialized.set(false) // Allow re-initialization if needed, though typically not recommended
            logMessage("Llama backend freed.")
        } else if (backendFreed.get()) {
            logMessage("Llama backend already freed.")
        } else {
            // It's okay if it was never initialized, just log it.
            logMessage("Llama backend was never initialized, cannot free.")
        }
    }

    // Private helper for logging using Android Logcat
    // TODO: Add dependency on Android core library if not already present
    private fun logMessage(message: String, isError: Boolean = false) {
        val tag = "LlamaPlatform"
        if (isError) {
            android.util.Log.e(tag, message)
        } else {
            android.util.Log.i(tag, message)
        }
    }
} 