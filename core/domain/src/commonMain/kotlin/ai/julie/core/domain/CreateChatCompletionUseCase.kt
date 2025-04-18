package ai.julie.core.domain

import ai.julie.core.data.OpenAiRepository
import ai.julie.core.model.chatcompletion.ChatCompletion
import ai.julie.core.model.chatcompletion.create.CreateChatCompletion
import ai.julie.core.model.chatcompletion.create.message.content.MessageContentRequest
import ai.julie.core.model.chatcompletion.create.message.UserMessageRequest

class CreateChatCompletionUseCase(
    private val openAiRepository: OpenAiRepository,
) {
    suspend operator fun invoke(): ChatCompletion {
        val test = CreateChatCompletion(
            model = "gpt-4.1",
            messages = listOf(
                UserMessageRequest(
                    content = MessageContentRequest.Value("Hello, how are you?")
                ),
            )
        )
        return openAiRepository.createChatCompletion(test)
    }
}