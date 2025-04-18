package ai.julie.core.network.datasource

import ai.julie.core.model.chatcompletion.ChatCompletion
import ai.julie.core.model.chatcompletion.create.CreateChatCompletion

interface OpenAiDataSource {

    suspend fun createChatCompletion(request: CreateChatCompletion): ChatCompletion
}