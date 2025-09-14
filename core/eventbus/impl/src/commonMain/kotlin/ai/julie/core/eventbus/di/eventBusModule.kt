package ai.julie.core.eventbus.di

import ai.julie.core.eventbus.EventBus
import ai.julie.core.eventbus.EventBusImpl
import org.koin.dsl.module

val eventBusModule = module {
    single<EventBus> { EventBusImpl() }
}