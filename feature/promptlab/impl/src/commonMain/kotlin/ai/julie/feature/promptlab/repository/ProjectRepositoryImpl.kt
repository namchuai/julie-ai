package ai.julie.feature.promptlab.data.repository

import ai.julie.feature.promptlab.data.mapper.toDocument
import ai.julie.feature.promptlab.data.mapper.toProject
import ai.julie.feature.promptlab.model.Project
import ai.julie.feature.promptlab.repository.ProjectRepository

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

class ProjectRepositoryImpl(
    private val dbName: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProjectRepository {

    companion object {
        const val COLLECTION_NAME = "projects"
        const val SCOPE_NAME = "promptlab"
    }

    private fun getCollection(): Collection? =
        Database(dbName).getCollection(COLLECTION_NAME, SCOPE_NAME)

    init {
        Database(dbName).createCollection(COLLECTION_NAME, SCOPE_NAME)
    }

    override suspend fun create(project: Project): Project = withContext(dispatcher) {
        val db = Database(dbName)
        db.getCollection(COLLECTION_NAME, SCOPE_NAME)?.save(project.toDocument())
        db.close()
        project
    }

    override suspend fun update(project: Project): Project = create(project)

    override suspend fun delete(id: String) = withContext(dispatcher) {
        val db = Database(dbName)
        db.getCollection(COLLECTION_NAME, SCOPE_NAME)?.getDocument(id)?.let { doc ->
            db.getCollection(COLLECTION_NAME, SCOPE_NAME)?.delete(doc)
        }
        db.close()
    }

    override suspend fun getById(id: String): Project? = withContext(dispatcher) {
        val db = Database(dbName)
        val project = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
            ?.getDocument(id)
            ?.toProject()
        db.close()
        project
    }

    override suspend fun getByWorkspaceId(workspaceId: String): List<Project> =
        withContext(dispatcher) {
            val db = Database(dbName)
            val collection =
                db.getCollection(COLLECTION_NAME, SCOPE_NAME) ?: return@withContext emptyList()

            val query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.collection(collection))
                .where(Expression.property("workspaceId").equalTo(Expression.string(workspaceId)))

            val projects = query.execute().allResults().mapNotNull { result ->
                val docId = result.getString(0)
                docId?.let { id ->
                    collection.getDocument(id)?.toProject()
                }
            }

            db.close()
            projects
        }

    override fun observeByWorkspaceId(workspaceId: String): Flow<List<Project>> {
        val collection = getCollection() ?: return flowOf(emptyList())

        val query = QueryBuilder.select(SelectResult.expression(Meta.id))
            .from(DataSource.collection(collection))
            .where(Expression.property("workspaceId").equalTo(Expression.string(workspaceId)))

        return query.queryChangeFlow()
            .map { change ->
                change.results?.allResults()?.mapNotNull { result ->
                    val docId = result.getString(0)
                    docId?.let { id ->
                        collection.getDocument(id)?.toProject()
                    }
                } ?: emptyList()
            }
            .flowOn(dispatcher)
    }

    override fun observeById(id: String): Flow<Project?> {
        val collection = getCollection() ?: return flowOf(null)

        val query = QueryBuilder.select(SelectResult.expression(Meta.id))
            .from(DataSource.collection(collection))
            .where(Meta.id.equalTo(Expression.string(id)))

        return query.queryChangeFlow()
            .map { change ->
                change.results?.allResults()?.firstOrNull()?.let {
                    collection.getDocument(id)?.toProject()
                }
            }
            .flowOn(dispatcher)
    }
}