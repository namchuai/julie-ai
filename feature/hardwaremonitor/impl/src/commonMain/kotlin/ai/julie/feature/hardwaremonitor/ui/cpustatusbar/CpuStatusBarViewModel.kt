package ai.julie.feature.hardwaremonitor.ui.cpustatusbar

import ai.julie.core.common.AsyncState
import ai.julie.core.common.createAsyncLoading
import ai.julie.core.common.createAsyncSuccess
import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import ai.julie.feature.hardwaremonitor.domain.FlowOfCpuUsage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class CpuStatusBarViewModel(
    private val flowOfCpuUsage: FlowOfCpuUsage,
) : ViewModel() {

    val state = viewModelState<AsyncState<CpuStatusBarState>>(
        savedStateBehaviour = doNotSaveState(),
        loadTimeReporter = doNotReportLoadTime(),
        initialState = createAsyncLoading(),
    )

    init {
        flowOfCpuUsage.flowOfCpuUsage()
            .onEach { cpuUsage ->
                state.update {
                    createAsyncSuccess(
                        CpuStatusBarState(
                            percentage = cpuUsage,
                        )
                    )
                }
            }.launchIn(viewModelScope)
    }
}