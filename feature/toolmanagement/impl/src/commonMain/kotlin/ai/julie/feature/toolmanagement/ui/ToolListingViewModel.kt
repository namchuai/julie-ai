package ai.julie.feature.toolmanagement.ui

import ai.julie.core.common.AsyncState
import ai.julie.core.common.createAsyncLoading
import ai.julie.core.common.createAsyncSuccess
import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import ai.julie.feature.toolmanagement.domain.CreateTool
import ai.julie.feature.toolmanagement.domain.DeleteTool
import ai.julie.feature.toolmanagement.domain.EnrichedTool
import ai.julie.feature.toolmanagement.domain.FlowOfTools
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ToolListingViewModel(
    private val flowOfTools: FlowOfTools,
    private val deleteTool: DeleteTool,
    private val createTool: CreateTool,
) : ViewModel() {

    val state = viewModelState<AsyncState<ToolListingState>>(
        savedStateBehaviour = doNotSaveState(),
        initialState = createAsyncLoading(),
        loadTimeReporter = doNotReportLoadTime(),
    )

    fun onCreateTool(tool: EnrichedTool) {
        viewModelScope.launch {
            createTool.createTool(tool)
        }
    }

    fun onDeleteTool(toolId: String) {
        viewModelScope.launch {
            deleteTool.deleteTool(toolId)
        }
    }

    init {
        flowOfTools.flowOfTools()
            .onEach { tools ->
                state.update {
                    createAsyncSuccess(
                        value = ToolListingState(
                            tools = tools
                        )
                    )
                }
            }.launchIn(viewModelScope)
    }
}