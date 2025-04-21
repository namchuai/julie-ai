package ai.julie.feature.modelmarket.screen.modelmarket

data class ModelMarketState(
    val downloadUrl: String = "",
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val downloadError: String? = null
)
