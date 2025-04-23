package ai.julie.core.network.di

import ai.julie.core.network.HttpClientProvider
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
}