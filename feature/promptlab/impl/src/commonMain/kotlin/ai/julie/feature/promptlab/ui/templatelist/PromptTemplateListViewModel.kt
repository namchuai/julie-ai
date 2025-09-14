package ai.julie.feature.promptlab.ui.templatelist

import ai.julie.core.common.AsyncState
import ai.julie.core.common.createAsyncLoading
import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import ai.julie.feature.promptlab.domain.FlowOfPromptTemplates
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class PromptTemplateListViewModel(
    private val flowOfPromptTemplates: FlowOfPromptTemplates,
) : ViewModel() {

    val state = viewModelState<AsyncState<PromptTemplateListUiState>>(
        savedStateBehaviour = doNotSaveState(),
        initialState = createAsyncLoading(),
        loadTimeReporter = doNotReportLoadTime(),
    )

    init {
        flowOfPromptTemplates.flowOfPromptTemplates()
            .onEach {

            }
            .launchIn(viewModelScope)
    }
}
