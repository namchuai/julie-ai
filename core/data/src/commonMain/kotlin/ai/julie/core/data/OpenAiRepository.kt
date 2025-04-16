package ai.julie.core.data

import ai.julie.core.model.chatcompletionresponse.ChatCompletion
import ai.julie.core.model.createchatcompletion.CreateChatCompletionRequestBody

interface OpenAiRepository {
    suspend fun createChatCompletionNonStream(input: CreateChatCompletionRequestBody): ChatCompletion
}