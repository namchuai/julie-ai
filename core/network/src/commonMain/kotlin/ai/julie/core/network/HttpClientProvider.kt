package ai.julie.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HttpClientProvider {
    fun getHttpClient(config: HttpConfig) = HttpClient {
        when (config.protocol) {
            "https" -> {
                require(config.host.isNotBlank()) { "Host cannot be null or blank" }
                require(!config.host.contains("https")) { "Host should not contain https" }

                install(DefaultRequest) {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = config.host
                    }

                    config.headers.forEach {
                        header(it.key, it.value)
                    }
                }
            }

            else -> throw IllegalArgumentException("Unsupported protocol: ${config.protocol}")
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                explicitNulls = false
                prettyPrint = true
            })
        }
        install(HttpCache)
        install(Logging) {
            level = LogLevel.ALL
        }
    }
}