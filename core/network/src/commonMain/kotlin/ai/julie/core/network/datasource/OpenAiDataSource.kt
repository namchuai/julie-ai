package ai.julie.core.network.datasource

import ai.julie.core.network.model.request.CreateChatCompletionRequest
import ai.julie.core.network.model.createchatcompletion.response.ChatCompletion

interface OpenAiDataSource {

    suspend fun createChatCompletion(request: CreateChatCompletionRequest): ChatCompletion
}