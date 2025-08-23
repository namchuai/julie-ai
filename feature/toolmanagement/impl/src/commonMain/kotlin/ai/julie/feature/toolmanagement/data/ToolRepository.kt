package ai.julie.feature.toolmanagement.data

import ai.julie.feature.toolmanagement.domain.*
import kotbase.DataSource
import kotbase.Database
import kotbase.Meta
import kotbase.Ordering
import kotbase.QueryBuilder
import kotbase.SelectResult
import kotbase.queryChangeFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class ToolRepository private constructor(
    private val dbName: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CreateTool, FlowOfTools, UpdateTool, DeleteTool {
    
    override suspend fun createTool(tool: EnrichedTool): String {
        return withContext(dispatcher) {
            val db = Database(dbName)
            db.getCollection(COLLECTION_NAME, SCOPE_NAME)?.save(tool.toDocument())
            db.close()
            tool.function.name
        }
    }
    
    override fun flowOfTools(): Flow<List<EnrichedTool>> {
        val collection = Database(dbName).getCollection(COLLECTION_NAME, SCOPE_NAME)
            ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        
        val query = QueryBuilder.select(SelectResult.expression(Meta.id))
            .from(DataSource.collection(collection))
            .orderBy(Ordering.property("updated_at").descending())
        
        return query.queryChangeFlow()
            .map { change ->
                change.results?.allResults()?.mapNotNull { result ->
                    val docId = result.getString(0)
                    docId?.let { id ->
                        collection.getDocument(id)?.toEnrichedTool()
                    }
                } ?: emptyList()
            }
            .flowOn(dispatcher)
    }
    
    override suspend fun updateTool(tool: EnrichedTool) = withContext(dispatcher) {
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
        collection?.save(tool.copy(updatedAt = Clock.System.now().toEpochMilliseconds()).toDocument())
        db.close()
    }
    
    override suspend fun deleteTool(toolId: String) = withContext(dispatcher) {
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
        collection?.getDocument(toolId)?.let { document ->
            collection.delete(document)
        }
        db.close()
    }
    
    private fun initializeTools() {
        val db = Database(dbName)
        db.createCollection(COLLECTION_NAME, SCOPE_NAME)
        db.close()
    }
    
    companion object {
        const val COLLECTION_NAME = "tools"
        const val SCOPE_NAME = "sync"
        
        fun create(
            dbName: String,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): ToolRepository {
            val repository = ToolRepository(dbName, dispatcher)
            repository.initializeTools()
            return repository
        }
    }
}