package ai.julie.feature.chat

import ai.julie.core.domain.CreateChatCompletionUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val createChatCompletionUseCase: CreateChatCompletionUseCase,
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

        viewModelScope.launch {
            val chatCompletion =
                createChatCompletionUseCase(messages = _uiState.value.messages.map {
                    ChatMessage(
                        role = if (it.isFromUser) Role.User else Role.Assistant,
                        content = it.content
                    )
                })
            _uiState.update {
                it.copy(
                    messages = it.messages + MessageItem(
                        id = it.messages.size.toString(),
                        content = chatCompletion.choices.first().message.content ?: "",
                        isFromUser = false,
                    )
                )
            }
        }
    }

    fun onNewChatClick() {
    }

    fun onMessageUpdate(messageInput: String) {
        _uiState.update {
            it.copy(message = messageInput)
        }
    }
}