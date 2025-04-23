package ai.julie.core.domain

import ai.julie.core.data.OpenAiRepository
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.model.ModelId

class CreateChatCompletionUseCase(
    private val openAiRepository: OpenAiRepository,
) {
    suspend operator fun invoke(
        messages: List<ChatMessage>,
    ): ChatCompletion {
        return openAiRepository.createChatCompletion(
            ChatCompletionRequest(
                model = ModelId(id = "gpt-4.1"),
                messages = messages
            )
        )
    }
}