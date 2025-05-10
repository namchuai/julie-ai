package ai.julie.llamabinding

/**
 * Callback interface for receiving progress updates during model loading.
 */
fun interface LlamaProgressCallback {
    /**
     * Called during model loading to report progress.
     *
     * @param progress Progress value between 0.0 and 1.0
     * @return true to continue loading, false to abort
     */
    fun onProgress(progress: Float): Boolean
}