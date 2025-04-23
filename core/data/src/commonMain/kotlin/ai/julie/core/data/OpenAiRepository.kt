package ai.julie.core.data

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest

interface OpenAiRepository {
    suspend fun createChatCompletion(input: ChatCompletionRequest): ChatCompletion
}