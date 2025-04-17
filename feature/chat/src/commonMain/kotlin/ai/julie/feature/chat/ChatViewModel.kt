package ai.julie.feature.chat

import ai.julie.core.domain.CreateChatCompletionUseCase
import ai.julie.logging.Logger
import ai.julie.storage.Database
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChatViewModel(
    private val createChatCompletionUseCase: CreateChatCompletionUseCase,
    private val database: Database,
) : ViewModel() {

    fun onSendClick() {
        viewModelScope.launch {
            val result = createChatCompletionUseCase()
            Logger.i { result.toString() }
        }
    }

    fun onTestStorageClick() {
        viewModelScope.launch {
            val launches = database.getAllLaunches()
            Logger.i { launches.toString() }
        }
    }
}