package ai.julie.feature.modelmarket.screen.modelmarket

import ai.julie.core.domain.model.download.DownloadInfo
import ai.julie.core.domain.model.download.DownloadModelViaUrlUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ModelMarketViewModel(
    private val downloadModelViaUrlUseCase: DownloadModelViaUrlUseCase,
) : ViewModel() {

    private var _uiState = MutableStateFlow(ModelMarketState())
    val uiState: StateFlow<ModelMarketState> = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ModelMarketState()
        )

    fun onDownloadUrlUpdate(downloadUrl: String) {
        _uiState.update {
            it.copy(
                downloadUrl = downloadUrl,
                downloadError = null // Clear error when URL changes
            )
        }
    }

    fun onStartDownloadClick() {
        val url = _uiState.value.downloadUrl
        if (url.isBlank()) {
            _uiState.update { it.copy(downloadError = "Download URL cannot be empty.") }
            return
        }

        // Extract filename from URL
        val filename =
            url.substringAfterLast('/').substringBefore('?') // Handle potential query params
        if (filename.isBlank()) {
            _uiState.update { it.copy(downloadError = "Could not determine filename from URL.") }
            return
        }

        // Base directory (Note: This path is likely Android-specific)
        val baseDir = "/data/data/ai.julie/files/models/"
        val destinationPath = "$baseDir$filename"

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDownloading = true,
                    downloadProgress = 0f,
                    downloadError = null
                )
            }

            downloadModelViaUrlUseCase(url, destinationPath)
                .onEach { downloadInfo ->
                    _uiState.update { state ->
                        when (downloadInfo) {
                            is DownloadInfo.Downloading -> state.copy(
                                downloadProgress = downloadInfo.progress,
                                isDownloading = true // Ensure downloading state is maintained
                            )

                            DownloadInfo.Success -> state.copy(
                                isDownloading = false,
                                downloadProgress = 1f, // Ensure progress shows 100%
                                downloadError = null // Clear any previous error
                            )

                            is DownloadInfo.Error -> state.copy(
                                isDownloading = false,
                                downloadError = downloadInfo.message
                            )

                            DownloadInfo.Idle -> state.copy( // Handle Idle case
                                isDownloading = false,
                                downloadProgress = 0f,
                                downloadError = null
                            )
                        }
                    }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isDownloading = false,
                            downloadError = e.message
                                ?: "An unexpected error occurred during download."
                        )
                    }
                }
                .launchIn(viewModelScope) // Launch collection in viewModelScope
        }
    }
}