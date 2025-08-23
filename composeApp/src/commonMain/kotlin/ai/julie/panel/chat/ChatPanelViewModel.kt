package ai.julie.panel.chat

import ai.julie.core.common.AsyncState
import ai.julie.core.common.createAsyncLoading
import ai.julie.core.common.createAsyncSuccess
import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import ai.julie.feature.thread.domain.FlowOfActiveThread
import ai.julie.feature.thread.domain.FlowOfThreads
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ChatPanelViewModel(
    private val flowOfActiveThread: FlowOfActiveThread,
    private val flowOfThreads: FlowOfThreads,
) : ViewModel() {

    val state = viewModelState<AsyncState<ChatPanelState>>(
        savedStateBehaviour = doNotSaveState(),
        loadTimeReporter = doNotReportLoadTime(),
        initialState = createAsyncLoading(),
    )

    init {
        combine(
            flowOfThreads.flowOfThreads(),
            flowOfActiveThread.flowOfActiveThread()
        ) { threads, activeThread ->
            ChatPanelState(
                activeThread = activeThread,
                threads = threads
            )
        }.onEach { chatPanelState ->
            state.update {
                createAsyncSuccess(chatPanelState)
            }
        }.launchIn(viewModelScope)
    }
}
