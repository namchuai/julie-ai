package ai.julie.core.data.llama

fun interface InitializeLlamaBackend {
    suspend fun initialize()
}