package ai.julie.core.data

import ai.julie.core.model.chatcompletion.ChatCompletion
import ai.julie.core.model.chatcompletion.create.CreateChatCompletion

interface OpenAiRepository {
    suspend fun createChatCompletion(input: CreateChatCompletion): ChatCompletion
}