package ai.julie.core.network.di

import io.ktor.client.HttpClient
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

    single {
        HttpClient()
    }
}