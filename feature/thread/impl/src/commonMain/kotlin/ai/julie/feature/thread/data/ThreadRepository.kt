package ai.julie.feature.thread.data

import ai.julie.feature.thread.domain.ClearActiveThread
import ai.julie.feature.thread.domain.CreateThread
import ai.julie.feature.thread.domain.DeleteThread
import ai.julie.feature.thread.domain.FlowOfActiveThread
import ai.julie.feature.thread.domain.FlowOfThread
import ai.julie.feature.thread.domain.FlowOfThreads
import ai.julie.feature.thread.domain.SetActiveThread
import ai.julie.feature.thread.domain.UpdateThreadSamplingPresetId
import ai.julie.feature.thread.domain.UpdateThreadTimestamp
import ai.julie.feature.thread.domain.model.EnrichedThread
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadId
import kotbase.DataSource
import kotbase.Database
import kotbase.Expression
import kotbase.Meta
import kotbase.Ordering
import kotbase.QueryBuilder
import kotbase.SelectResult
import kotbase.queryChangeFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlin.uuid.Uuid

class ThreadRepository private constructor(
    private val dbName: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CreateThread,
    DeleteThread,
    SetActiveThread,
    ClearActiveThread,
    UpdateThreadTimestamp,
    FlowOfActiveThread,
    FlowOfThreads,
    FlowOfThread,
    UpdateThreadSamplingPresetId {

    private val activeThreadId = MutableStateFlow<String?>(null)

    override suspend fun createThread(modelId: String, title: String?): EnrichedThread {
        val createdAt = Clock.System.now().epochSeconds.toInt()
        val thread = EnrichedThread(
            thread = Thread(
                id = ThreadId(Uuid.random().toHexString()),
                objectType = "thread",
                createdAt = createdAt,
//                toolResources = toolResources,
                metadata = emptyMap(),
            ),
            modelId = modelId,
            updatedAt = createdAt,
        )

        return withContext(dispatcher) {
            val db = Database(dbName)
            db.getCollection(COLLECTION_NAME, SCOPE_NAME)?.save(thread.toDocument())
            db.close()
            thread
        }
    }

    override fun flowOfThreads(): Flow<List<EnrichedThread>> {
        val collection = Database(dbName).getCollection(COLLECTION_NAME, SCOPE_NAME)
            ?: return flowOf(emptyList())

        val query = QueryBuilder.select(SelectResult.expression(Meta.id))
            .from(DataSource.collection(collection))
            .orderBy(Ordering.property("updated_at").descending())

        return query.queryChangeFlow()
            .map { change ->
                change.results?.allResults()?.mapNotNull { result ->
                    val docId = result.getString(0)
                    docId?.let { id ->
                        collection.getDocument(id)?.toEnrichedThread()
                    }
                } ?: emptyList()
            }
            .flowOn(dispatcher)
    }

    override suspend fun deleteThread(threadId: String) = withContext(dispatcher) {
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
        collection?.getDocument(threadId)?.let { document ->
            collection.delete(document)
        }
        db.close()

        // Clear active thread if it was the deleted thread
        if (activeThreadId.value == threadId) {
            activeThreadId.value = null
        }
    }

    override fun setActiveThreadId(threadId: String) {
        activeThreadId.value = threadId
    }

    override fun flowOfActiveThread(): Flow<EnrichedThread?> {
        return activeThreadId.flatMapLatest { threadId ->
            if (threadId != null) {
                flowOfThread(threadId)
            } else {
                flowOf(null)
            }
        }.distinctUntilChanged()
    }

    override suspend fun clearActiveThread() {
        activeThreadId.value = null
    }

    override suspend fun updateThreadTimestamp(threadId: String) = withContext(dispatcher) {
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
        collection?.getDocument(threadId)?.let { document ->
            val mutableDoc = document.toMutable()
            mutableDoc.setInt("updated_at", Clock.System.now().epochSeconds.toInt())
            collection.save(mutableDoc)
        }
        db.close()
    }

    private fun initializeThread() {
        Database(dbName).createCollection(COLLECTION_NAME, SCOPE_NAME)
    }

    override suspend fun updateThreadSamplingPresetId(threadId: String, samplingPresetId: String) {
        withContext(dispatcher) {
            val db = Database(dbName)
            val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
            collection?.getDocument(threadId)?.let { document ->
                val mutableDoc = document.toMutable()
                mutableDoc.setString("sampling_preset_id", samplingPresetId) // TODO: bad hardcode
                collection.save(mutableDoc)
            }
            db.close()
        }
    }

    override fun flowOfThread(threadId: String): Flow<EnrichedThread?> {
        val collection = Database(dbName).getCollection(COLLECTION_NAME, SCOPE_NAME)
            ?: return flowOf(null)

        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
            .where(Meta.id.equalTo(Expression.string(threadId)))

        return query.queryChangeFlow()
            .map { collection.getDocument(threadId)?.toEnrichedThread() }
            .flowOn(dispatcher)
    }

    companion object {
        const val COLLECTION_NAME = "threads"
        const val SCOPE_NAME = "sync"

        fun create(
            dbName: String,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): ThreadRepository {
            val repository = ThreadRepository(dbName, dispatcher)
            repository.initializeThread()
            return repository
        }
    }
}