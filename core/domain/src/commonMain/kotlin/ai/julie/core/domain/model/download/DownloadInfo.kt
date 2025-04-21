package ai.julie.core.domain.model.download

sealed class DownloadInfo {
    data object Idle : DownloadInfo()
    data class Downloading(val progress: Float) : DownloadInfo()
    data object Success : DownloadInfo()
    data class Error(val message: String) : DownloadInfo()
} 