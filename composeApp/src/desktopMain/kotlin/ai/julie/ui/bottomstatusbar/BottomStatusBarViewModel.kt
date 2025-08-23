package ai.julie.ui.bottomstatusbar

import ai.julie.core.common.AsyncState
import ai.julie.core.common.createAsyncLoading
import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import ai.julie.feature.modelmanagement.domain.FlowOfRunningModels
import ai.julie.feature.modelmanagement.domain.FlowOfStartingModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class BottomStatusBarViewModel(
    private val flowOfStartingModels: FlowOfStartingModels,
    private val flowOfRunningModels: FlowOfRunningModels,
) : ViewModel() {

    val state = viewModelState<AsyncState<BottomStatusBarState>>(
        savedStateBehaviour = doNotSaveState(),
        initialState = createAsyncLoading(),
        loadTimeReporter = doNotReportLoadTime(),
    )

    init {
        combine(
            flowOfStartingModels.flowOfStartingModels(),
            flowOfRunningModels.flowOfRunningModels()
        ) { startingModels, runningModels ->
            val appVersion = "1.0.0" // TODO: Replace with actual app version retrieval logic
            val buildNo = "100" // TODO: Replace with actual build number retrieval logic

            AsyncState.Success(
                BottomStatusBarState(
                    appVersion = appVersion,
                    buildNo = buildNo,
                    startingModels = startingModels,
                    runningModels = runningModels
                )
            )
        }.onEach { newState ->
            state.update { newState }
        }.launchIn(viewModelScope)
    }
}
