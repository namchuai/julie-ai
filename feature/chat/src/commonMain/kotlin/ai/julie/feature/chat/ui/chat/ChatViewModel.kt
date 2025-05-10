package ai.julie.feature.chat.ui.chat

import ai.julie.core.common.AsyncState
import ai.julie.core.common.createAsyncLoading
import ai.julie.core.common.createAsyncSuccess
import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import ai.julie.feature.message.domain.FlowOfMessages
import ai.julie.feature.thread.domain.FlowOfActiveThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ChatViewModel(
    private val flowOfActiveThread: FlowOfActiveThread,
    private val flowOfMessages: FlowOfMessages,
) : ViewModel() {

    val state = viewModelState<AsyncState<ChatState>>(
        savedStateBehaviour = doNotSaveState(),
        loadTimeReporter = doNotReportLoadTime(),
        initialState = createAsyncLoading(),
    )

    init {
        flowOfActiveThread.flowOfActiveThread().flatMapLatest { thread ->
            if (thread == null) {
                flowOf(emptyList())
            } else {
                flowOfMessages.flowOfMessages(threadId = thread.id)
            }
        }.onEach { messages ->
            state.update {
                createAsyncSuccess(
                    ChatState(
                        messages = messages,
                    )
                )
            }
        }.launchIn(viewModelScope)
    }
}