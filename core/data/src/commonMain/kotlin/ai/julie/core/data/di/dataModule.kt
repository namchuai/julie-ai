package ai.julie.core.data.di

import ai.julie.core.network.di.networkModule
import org.koin.dsl.module

val dataModule = module {
    includes(networkModule)
}