package ai.julie.core.data.thread

import ai.julie.storage.JulieDatabase
import com.aallam.openai.api.thread.Thread

class ThreadRepositoryImpl(
    private val database: JulieDatabase,
) : ThreadRepository {

    override suspend fun storeThread(thread: Thread) = database.storeThread(thread)

    override fun threadFlow() = database.threadFlow()
}