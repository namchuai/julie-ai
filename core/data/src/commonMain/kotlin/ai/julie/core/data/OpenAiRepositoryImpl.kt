package ai.julie.core.data

import ai.julie.core.network.HttpClientProvider
import ai.julie.core.network.HttpConfig
import ai.julie.logging.Logger
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class OpenAiRepositoryImpl(
    private val httpClientProvider: HttpClientProvider,
) : OpenAiRepository {
    private val tmpConfig = HttpConfig(
        protocol = "https",
        host = "api.openai.com",
        headers = mapOf(
            HttpHeaders.ContentType to "application/json",
            HttpHeaders.Authorization to "Bearer placeholder",
        )
    )

    override suspend fun createChatCompletion(input: ChatCompletionRequest): ChatCompletion {
        val response = httpClientProvider.getHttpClient(tmpConfig).post("v1/chat/completions") {
            contentType(ContentType.Application.Json)
            setBody(input)
        }

        return try {
            response.body<ChatCompletion>()
        } catch (e: Exception) {
            Logger.e { "Error parsing response: ${e.message}" }
            throw e
        }
    }
}