package ai.julie.feature.chat

data class MessageItem(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
)

data class ChatModel(
    val message: String = "",
    val messages: List<MessageItem> = emptyList(),
)