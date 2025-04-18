package ai.julie.core.data

import ai.julie.core.model.chatcompletion.create.CreateChatCompletion
import ai.julie.core.network.datasource.OpenAiDataSource

class OpenAiRepositoryImpl(
    private val dataSource: OpenAiDataSource,
) : OpenAiRepository {
    override suspend fun createChatCompletion(input: CreateChatCompletion) =
        dataSource.createChatCompletion(input)
}