package ai.julie.feature.chat.ui.chat

import ai.julie.feature.message.domain.model.EnrichedMessage
import ai.julie.feature.thread.domain.model.EnrichedThread

data class ChatState(
    val messages: List<EnrichedMessage>,
    val activeThread: EnrichedThread? = null,
    val activeConversationId: String? = null,
)

/**
 * Voice input state for the chat
 */
sealed class VoiceInputState {
    object Idle : VoiceInputState()
    object CheckingPermissions : VoiceInputState()
    object PermissionDenied : VoiceInputState()
    object CheckingModel : VoiceInputState()
    data class DownloadingModel(val progress: Float) : VoiceInputState()
    object Recording : VoiceInputState()
    object Processing : VoiceInputState()
    data class Error(val message: String) : VoiceInputState()
}
