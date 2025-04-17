package ai.julie.feature.chat

import ai.julie.core.domain.CreateChatCompletionUseCase
import ai.julie.logging.Logger
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChatViewModel(
    private val createChatCompletionUseCase: CreateChatCompletionUseCase,
) : ViewModel() {

    fun onSendClick() {
        viewModelScope.launch {
            val result = createChatCompletionUseCase()
            Logger.i { result.toString() }
        }
    }
}