package ai.julie.core.data

import ai.julie.core.model.chatcompletionresponse.ChatCompletion
import ai.julie.core.model.createchatcompletion.CreateChatCompletionRequestBody
import ai.julie.core.network.datasource.OpenAiDataSource
import ai.julie.core.network.model.createchatcompletion.response.toChatCompletion
import ai.julie.core.network.model.request.toCreateChatCompletionRequest

class OpenAiRepositoryImpl(
    private val dataSource: OpenAiDataSource,
) : OpenAiRepository {
    override suspend fun createChatCompletionNonStream(input: CreateChatCompletionRequestBody): ChatCompletion {
        return dataSource.createChatCompletion(input.toCreateChatCompletionRequest())
            .toChatCompletion()
    }
}