package ai.julie.feature.chat.ui.chatinput

import ai.julie.core.domain.session.PromptSessionState

data class ChatInputState(
    val message: String,
    val sessionState: PromptSessionState = PromptSessionState.Idle,
)