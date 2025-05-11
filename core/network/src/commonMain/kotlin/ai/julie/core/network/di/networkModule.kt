package ai.julie.core.network.di

import ai.julie.core.network.HttpClientProvider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
            prettyPrint = true
        }
    }

    single<HttpClientProvider> {
        HttpClientProvider()
    }

    single<HttpClient> {
        HttpClient {
            install(HttpTimeout) {
                val timeout = 30_000L
                requestTimeoutMillis = timeout
                connectTimeoutMillis = timeout
            }
        }
    }
}