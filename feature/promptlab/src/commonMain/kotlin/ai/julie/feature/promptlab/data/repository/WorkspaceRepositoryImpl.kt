package ai.julie.feature.promptlab.data.repository

import ai.julie.feature.promptlab.data.mapper.toDocument
import ai.julie.feature.promptlab.data.mapper.toWorkspace
import ai.julie.feature.promptlab.model.Workspace
import ai.julie.feature.promptlab.repository.WorkspaceRepository
import kotbase.Collection
import kotbase.DataSource
import kotbase.Database
import kotbase.Expression
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

class WorkspaceRepositoryImpl(
    private val dbName: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : WorkspaceRepository {

    companion object {
        const val COLLECTION_NAME = "workspaces"
        const val SCOPE_NAME = "promptlab"
    }

    private fun getCollection(): Collection? =
        Database(dbName).getCollection(COLLECTION_NAME, SCOPE_NAME)

    init {
        Database(dbName).createCollection(COLLECTION_NAME, SCOPE_NAME)
    }

    override suspend fun create(workspace: Workspace): Workspace = withContext(dispatcher) {
        val db = Database(dbName)
        db.getCollection(COLLECTION_NAME, SCOPE_NAME)?.save(workspace.toDocument())
        db.close()
        workspace
    }

    override suspend fun update(workspace: Workspace): Workspace = create(workspace)

    override suspend fun delete(id: String) = withContext(dispatcher) {
        val db = Database(dbName)
        db.getCollection(COLLECTION_NAME, SCOPE_NAME)?.getDocument(id)?.let { doc ->
            db.getCollection(COLLECTION_NAME, SCOPE_NAME)?.delete(doc)
        }
        db.close()
    }

    override suspend fun getById(id: String): Workspace? = withContext(dispatcher) {
        val db = Database(dbName)
        val workspace = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
            ?.getDocument(id)
            ?.toWorkspace()
        db.close()
        workspace
    }

    override fun observeAll(): Flow<List<Workspace>> {
        val collection = getCollection() ?: return flowOf(emptyList())

        val query = QueryBuilder.select(SelectResult.expression(Meta.id))
            .from(DataSource.collection(collection))

        return query.queryChangeFlow()
            .map { change ->
                change.results?.allResults()?.mapNotNull { result ->
                    val docId = result.getString(0)
                    docId?.let { id ->
                        collection.getDocument(id)?.toWorkspace()
                    }
                } ?: emptyList()
            }
            .flowOn(dispatcher)
    }

    override fun observeById(id: String): Flow<Workspace?> {
        val collection = getCollection() ?: return flowOf(null)

        val query = QueryBuilder.select(SelectResult.expression(Meta.id))
            .from(DataSource.collection(collection))
            .where(Meta.id.equalTo(Expression.string(id)))

        return query.queryChangeFlow()
            .map { change ->
                change.results?.allResults()?.firstOrNull()?.let {
                    collection.getDocument(id)?.toWorkspace()
                }
            }
            .flowOn(dispatcher)
    }
}