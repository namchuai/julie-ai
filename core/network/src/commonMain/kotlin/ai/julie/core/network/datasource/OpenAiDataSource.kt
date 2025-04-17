package ai.julie.core.network.datasource

import ai.julie.core.network.model.createchatcompletion.response.ChatCompletion
import ai.julie.core.network.model.request.CreateChatCompletionRequest

interface OpenAiDataSource {

    suspend fun createChatCompletion(request: CreateChatCompletionRequest): ChatCompletion
}