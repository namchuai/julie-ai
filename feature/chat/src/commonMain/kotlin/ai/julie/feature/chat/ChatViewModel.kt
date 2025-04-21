package ai.julie.feature.chat

import ai.julie.core.domain.CreateChatCompletionUseCase
import ai.julie.llamabinding.triggerTestLlamaLoad
import ai.julie.logging.Logger
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    private val createChatCompletionUseCase: CreateChatCompletionUseCase,
) : ViewModel() {

    private var _uiState = MutableStateFlow(
        ChatModel(
            messages = listOf(
                MessageItem(
                    id = "1",
                    content = "Hello, how can I assist you today?",
                    isFromUser = false,
                ),
                MessageItem(
                    id = "2",
                    content = "I am looking for information on Kotlin Multiplatform.",
                    isFromUser = true,
                ),
            )
        )
    )
    val uiState: StateFlow<ChatModel> = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ChatModel(
                messages = listOf(
                    MessageItem(
                        id = "1",
                        content = "Hello, how can I assist you today?",
                        isFromUser = false,
                    ),
                    MessageItem(
                        id = "2",
                        content = "I am looking for information on Kotlin Multiplatform.",
                        isFromUser = true,
                    ),
                )
            )
        )


    fun onSendClick() {
        viewModelScope.launch {
            val result = createChatCompletionUseCase()
            Logger.i { result.toString() }
        }
    }

    fun onTestLlamaLoadClick() {
        triggerTestLlamaLoad()
    }

    fun onNewChatClicked() {
    }

    fun onNewChatClick() {
    }

    fun onMessageUpdate(messageInput: String) {
    }

    fun loadChats() {
        // Placeholder - implement if needed
    }
}