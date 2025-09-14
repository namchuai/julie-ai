package ai.julie.core.eventbus

import kotlinx.coroutines.flow.Flow

interface EventBus {
    fun emit(event: Event)
    fun events(): Flow<Event>
}