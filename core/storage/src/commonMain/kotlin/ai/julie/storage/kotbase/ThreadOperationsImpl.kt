package ai.julie.storage.kotbase

import ai.julie.storage.ThreadOperations
import ai.julie.storage.kotbase.util.toDocument
import ai.julie.storage.kotbase.util.toThread
import com.aallam.openai.api.thread.Thread
import kotbase.DataSource
import kotbase.Database
import kotbase.Meta
import kotbase.QueryBuilder
import kotbase.SelectResult
import kotbase.queryChangeFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ThreadOperationsImpl private constructor(
    private val dbName: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ThreadOperations {

    override suspend fun storeThread(thread: Thread) = withContext(dispatcher) {
        val db = Database(dbName)
        db.getCollection(COLLECTION_NAME, SCOPE_NAME)?.save(thread.toDocument())
        db.close()
        return@withContext thread
    }

    override fun threadFlow(): Flow<List<Thread>> {
        val collection = Database(dbName).getCollection(COLLECTION_NAME, SCOPE_NAME)
            ?: return flowOf(emptyList())

        val query = QueryBuilder.select(SelectResult.expression(Meta.id))
            .from(DataSource.collection(collection))

        return query.queryChangeFlow()
            .map { change ->
                change.results?.allResults()?.mapNotNull { result ->
                    val docId = result.getString(0)
                    docId?.let { id ->
                        collection.getDocument(id)?.toThread()
                    }
                } ?: emptyList()
            }
            .flowOn(dispatcher)
    }

    private fun initializeThread() {
        Database(dbName).createCollection(COLLECTION_NAME, SCOPE_NAME)
    }

    companion object {
        const val COLLECTION_NAME = "threads"
        const val SCOPE_NAME = "sync"

        fun create(
            dbName: String,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): ThreadOperations {
            val operations = ThreadOperationsImpl(dbName, dispatcher)
            operations.initializeThread()
            return operations
        }
    }
}