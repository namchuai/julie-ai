package ai.julie.feature.modelmarket.screen.modelmarket

import ai.julie.core.domain.model.download.DownloadModelViaUrlUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ModelMarketViewModel(
    private val downloadModelViaUrlUseCase: DownloadModelViaUrlUseCase,
) : ViewModel() {
    private var _uiState = MutableStateFlow(ModelMarketState())
    val uiState: StateFlow<ModelMarketState> = _uiState

    fun onDownloadUrlUpdate(downloadUrl: String) {
        _uiState.update {
            it.copy(
                downloadUrl = downloadUrl,
                downloadError = null
            )
        }
    }

    fun onDownloadClick() {
        val url = _uiState.value.downloadUrl
        if (url.isBlank()) {
            _uiState.update { it.copy(downloadError = "Download URL cannot be empty.") }
            return
        }
        viewModelScope.launch {
            val filename =
                url.substringAfterLast('/').substringBefore('?')
            if (filename.isBlank()) {
                _uiState.update { it.copy(downloadError = "Could not determine filename from URL.") }
                return@launch
            }

            downloadModelViaUrlUseCase.invoke(url)
        }
    }
}