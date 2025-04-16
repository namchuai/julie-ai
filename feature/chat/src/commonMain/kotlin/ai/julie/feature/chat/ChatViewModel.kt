package ai.julie.feature.chat

import ai.julie.core.domain.CreateChatCompletionUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChatViewModel(
    private val createChatCompletionUseCase: CreateChatCompletionUseCase,
) : ViewModel() {

    fun onSendClick() {
        viewModelScope.launch {
            val result = createChatCompletionUseCase()
            // Handle the result here, e.g., update UI state
            println(result)
        }
    }
}