package ai.julie.storage.kotbase

import ai.julie.storage.JulieDatabase
import ai.julie.storage.MessageOperations
import ai.julie.storage.ThreadOperations
import com.aallam.openai.api.thread.Thread
import kotlinx.coroutines.flow.Flow

class JulieDatabaseImpl(
    private val threadOperations: ThreadOperations,
    private val messageOperations: MessageOperations
) : JulieDatabase {

    override suspend fun storeThread(thread: Thread) = threadOperations.storeThread(thread)
    override fun threadFlow(): Flow<List<Thread>> = threadOperations.threadFlow()
}