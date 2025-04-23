package ai.julie.llamabinding

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Actual Desktop (JVM) implementation for LlamaPlatform.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object LlamaPlatform {
    private val backendInitialized = AtomicBoolean(false)
    private val backendFreed = AtomicBoolean(false)

    // Ensure NativeMethods class (and its init block) is loaded
    init {
        NativeMethods
    }

    /**
     * Initializes the Llama backend via JNI.
     * Ensures it's called only once.
     */
    actual fun initializeBackend() {
        if (!backendInitialized.getAndSet(true)) {
            if (backendFreed.get()) {
                // This should ideally not happen if usage is correct (free only on app exit)
                System.err.println("Warning: Llama backend initialized after being freed.")
                backendFreed.set(false) // Reset freed state if re-initializing
            }
            println("Initializing Llama backend...")
            NativeMethods.llama_backend_init()
            println("Llama backend initialized.")
        } else {
            println("Llama backend already initialized.")
        }
    }

    /**
     * Frees the Llama backend via JNI.
     * Ensures it's called only once and after initialization.
     */
    actual fun freeBackend() {
        if (backendInitialized.get() && !backendFreed.getAndSet(true)) {
            println("Freeing Llama backend...")
            NativeMethods.llama_backend_free()
            backendInitialized.set(false) // Allow re-initialization if needed, though typically not recommended
            println("Llama backend freed.")
        } else if (backendFreed.get()) {
            println("Llama backend already freed.")
        } else {
            println("Llama backend was never initialized.")
        }
    }
} 