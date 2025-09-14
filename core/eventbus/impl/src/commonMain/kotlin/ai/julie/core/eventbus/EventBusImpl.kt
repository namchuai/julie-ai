package ai.julie.core.eventbus

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class EventBusImpl : EventBus {
    private val _events = Channel<Event>(capacity = Channel.BUFFERED)
    
    override fun emit(event: Event) {
        _events.trySend(event)
    }
    
    override fun events(): Flow<Event> = _events.receiveAsFlow()
}