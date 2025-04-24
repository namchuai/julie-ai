package ai.julie.core.data.thread

import com.aallam.openai.api.thread.Thread
import kotlinx.coroutines.flow.Flow

interface ThreadRepository {

    suspend fun storeThread(thread: Thread): Thread

    fun threadFlow(): Flow<List<Thread>>
}