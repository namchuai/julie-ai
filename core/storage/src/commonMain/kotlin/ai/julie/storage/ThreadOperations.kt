package ai.julie.storage

import com.aallam.openai.api.thread.Thread
import kotlinx.coroutines.flow.Flow

interface ThreadOperations {

    suspend fun storeThread(thread: Thread): Thread

    fun threadFlow(): Flow<List<Thread>>
}