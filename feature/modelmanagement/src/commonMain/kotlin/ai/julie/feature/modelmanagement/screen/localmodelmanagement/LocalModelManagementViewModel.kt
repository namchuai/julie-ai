package ai.julie.feature.modelmanagement.screen.localmodelmanagement

import ai.julie.core.domain.modelmanagement.GetLocalAiModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocalModelManagementViewModel(
    private val getDownloadedGgufFiles: GetLocalAiModels,
) : ViewModel() {

    private val _uiState = MutableStateFlow(State())
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val models = getDownloadedGgufFiles()
            _uiState.update {
                it.copy(
                    models = models
                )
            }
        }
    }
}
