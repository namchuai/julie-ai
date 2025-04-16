package ai.julie.core.network.datasource

import ai.julie.core.network.model.createchatcompletion.response.ChatCompletion
import ai.julie.core.network.model.request.CreateChatCompletionRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class OpenAiDataSourceImpl(
    private val openAiHttpClient: HttpClient,
) : OpenAiDataSource {

    override suspend fun createChatCompletion(request: CreateChatCompletionRequest): ChatCompletion {
        // Serialize the request to JSON for logging
        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }
        val requestJson = json.encodeToString(CreateChatCompletionRequest.serializer(), request)
        println("Request URL: v1/chat/completions")
        println("Request body: $requestJson")

        val response = openAiHttpClient.post("v1/chat/completions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        val rawResponseBody = response.bodyAsText()
        println("Raw API response: $rawResponseBody")

        // Then parse it into the ChatCompletion object
        return try {
            response.body<ChatCompletion>()
        } catch (e: Exception) {
            println("Error parsing response: ${e.message}")
            throw e
        }
    }
}