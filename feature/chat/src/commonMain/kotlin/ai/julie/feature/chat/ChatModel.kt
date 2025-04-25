package ai.julie.feature.chat

import com.aallam.openai.api.thread.Thread

data class MessageItem(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
)

data class ChatModel(
    val message: String = "",
    val messages: List<MessageItem> = emptyList(),
    val threads: List<Thread> = emptyList(),
)