package ai.julie.core.domain

import ai.julie.core.data.OpenAiRepository
import ai.julie.core.model.chatcompletionresponse.ChatCompletion
import ai.julie.core.model.createchatcompletion.CreateChatCompletionRequestBody
import ai.julie.core.model.createchatcompletion.UserMessage

class CreateChatCompletionUseCase(
    private val openAiRepository: OpenAiRepository,
) {
    suspend operator fun invoke(): ChatCompletion {
        // TODO: fetching the messages
        val test = CreateChatCompletionRequestBody(
            model = "gpt-4.1",
            messages = listOf(
                UserMessage(
                    content = "Hello, how are you?",
                ),
            ),
        )
        return openAiRepository.createChatCompletionNonStream(test)
    }
}