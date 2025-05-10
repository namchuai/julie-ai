package ai.julie.feature.hardwaremonitor.ui.memorystatusbar

import ai.julie.core.common.AsyncState
import ai.julie.core.common.createAsyncLoading
import ai.julie.core.common.createAsyncSuccess
import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import ai.julie.feature.hardwaremonitor.domain.FlowOfMemoryUsage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class MemoryStatusBarViewModel(
    private val flowOfMemoryUsage: FlowOfMemoryUsage,
) : ViewModel() {

    val state = viewModelState<AsyncState<MemoryStatusBarState>>(
        savedStateBehaviour = doNotSaveState(),
        loadTimeReporter = doNotReportLoadTime(),
        initialState = createAsyncLoading(),
    )

    init {
        flowOfMemoryUsage.flowOfMemoryUsage()
            .onEach { memoryUsage ->
                state.update {
                    createAsyncSuccess(
                        MemoryStatusBarState(
                            percentage = memoryUsage,
                        )
                    )
                }
            }.launchIn(viewModelScope)
    }
}