package ai.julie.feature.thread.ui.threadlisting

import ai.julie.core.common.AsyncState
import ai.julie.core.common.createAsyncLoading
import ai.julie.core.common.createAsyncSuccess
import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import ai.julie.feature.thread.domain.DeleteThread
import ai.julie.feature.thread.domain.FlowOfActiveThread
import ai.julie.feature.thread.domain.FlowOfThreads
import ai.julie.feature.thread.domain.SetActiveThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ThreadListingViewModel(
    private val setActiveThread: SetActiveThread,
    private val flowOfThreads: FlowOfThreads,
    private val flowOfActiveThread: FlowOfActiveThread,
    private val deleteThread: DeleteThread,
) : ViewModel() {

    val state = viewModelState<AsyncState<ThreadListingState>>(
        savedStateBehaviour = doNotSaveState(),
        initialState = createAsyncLoading(),
        loadTimeReporter = doNotReportLoadTime(),
    )

    fun onThreadSelected(id: String) {
        setActiveThread.setActiveThreadId(id)
    }

    fun onDeleteThread(threadId: String) {
        viewModelScope.launch {
            deleteThread.deleteThread(threadId)
        }
    }

    init {
        combine(
            flowOfThreads.flowOfThreads(),
            flowOfActiveThread.flowOfActiveThread()
        ) { threads, activeThread ->
            state.update {
                createAsyncSuccess(
                    value = ThreadListingState(
                        threads = threads,
                        activeThread = activeThread,
                    )
                )
            }
        }.launchIn(viewModelScope)
    }
}