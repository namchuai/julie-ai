package ai.julie.llamabinding

/**
 * Declares platform-specific utility functions related to the Llama binding
 * that can be called from common code.
 */

/**
 * Triggers a test load of the Llama model on supported platforms (e.g., Desktop).
 * The implementation details are in the platform-specific `actual` function.
 */
expect fun triggerTestLlamaLoad()

/**
 * Clears any logs or state related to the Llama test on supported platforms.
 */
expect fun clearTestLlamaLog() 