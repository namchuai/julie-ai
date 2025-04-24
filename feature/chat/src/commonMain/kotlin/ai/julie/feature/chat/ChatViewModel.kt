package ai.julie.feature.chat

import ai.julie.core.domain.CreateChatCompletionStreamUseCase
import ai.julie.core.domain.CreateChatCompletionUseCase
import ai.julie.core.domain.thread.CreateThreadUseCase
import ai.julie.core.domain.thread.GetThreadFlowUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val createChatCompletionUseCase: CreateChatCompletionUseCase,
    private val createChatCompletionStreamUseCase: CreateChatCompletionStreamUseCase,
    private val storeThreadUseCase: CreateThreadUseCase,
    private val getThreadFlowUseCase: GetThreadFlowUseCase,
) : ViewModel() {

    private var _uiState = MutableStateFlow(ChatModel())
    val uiState: StateFlow<ChatModel> = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ChatModel()
        )

    fun onSendClick() {
        if (_uiState.value.message.isBlank()) return
        _uiState.update {
            it.copy(
                message = "",
                messages = it.messages + MessageItem(
                    id = it.messages.size.toString(),
                    content = it.message,
                    isFromUser = true,
                )
            )
        }

//        viewModelScope.launch {
//            val chatCompletion =
//                createChatCompletionUseCase(messages = _uiState.value.messages.map {
//                    ChatMessage(
//                        role = if (it.isFromUser) Role.User else Role.Assistant,
//                        content = it.content
//                    )
//                })
//            _uiState.update {
//                it.copy(
//                    messages = it.messages + MessageItem(
//                        id = it.messages.size.toString(),
//                        content = chatCompletion.choices.first().message.content ?: "",
//                        isFromUser = false,
//                    )
//                )
//            }
//        }

        viewModelScope.launch {
            createChatCompletionStreamUseCase.invoke(messages = _uiState.value.messages.map {
                ChatMessage(
                    role = if (it.isFromUser) Role.User else Role.Assistant,
                    content = it.content
                )
            }).collect { chunk ->
                val message = if (_uiState.value.messages.find { it.id == chunk.id } == null) {
                    MessageItem(
                        id = chunk.id,
                        content = chunk.choices.first().delta?.content ?: "",
                        isFromUser = false,
                    )
                } else {
                    MessageItem(
                        id = chunk.id,
                        content = _uiState.value.messages.find { it.id == chunk.id }?.content + chunk.choices.first().delta?.content
                            ?: "",
                        isFromUser = false,
                    )
                }

                _uiState.update {
                    it.copy(
                        messages = it.messages.filter { it.id != chunk.id } + message
                    )
                }
            }
        }
    }

    fun onNewChatClick() {
        viewModelScope.launch {
            storeThreadUseCase.invoke()
        }
    }

    fun onMessageUpdate(messageInput: String) {
        _uiState.update {
            it.copy(message = messageInput)
        }
    }

    init {
        viewModelScope.launch {
            getThreadFlowUseCase.invoke().onEach { threads ->
                _uiState.update {
                    it.copy(threads = threads)
                }
            }.collect()
        }
    }
}