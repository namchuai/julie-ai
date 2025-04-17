package ai.julie.core.network.datasource

import ai.julie.core.network.model.createchatcompletion.response.ChatCompletion
import ai.julie.core.network.model.request.CreateChatCompletionRequest
import ai.julie.logging.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class OpenAiDataSourceImpl(
    private val openAiHttpClient: HttpClient,
) : OpenAiDataSource {

    override suspend fun createChatCompletion(request: CreateChatCompletionRequest): ChatCompletion {
        val response = openAiHttpClient.post("v1/chat/completions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        return try {
            response.body<ChatCompletion>()
        } catch (e: Exception) {
            Logger.e { "Error parsing response: ${e.message}" }
            throw e
        }
    }
}