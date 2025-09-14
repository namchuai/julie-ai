package ai.julie.feature.promptlab.data

import ai.julie.feature.promptlab.domain.CreatePromptTemplate
import ai.julie.feature.promptlab.domain.DeletePromptTemplate
import ai.julie.feature.promptlab.domain.FlowOfPromptTemplates
import ai.julie.feature.promptlab.domain.UpdatePromptTemplate
import ai.julie.feature.promptlab.model.PromptTemplate
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlin.uuid.Uuid

class PromptTemplateRepository private constructor(
    private val dbName: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CreatePromptTemplate,
    DeletePromptTemplate,
    FlowOfPromptTemplates,
    UpdatePromptTemplate {

    override suspend fun createPromptTemplate(
        name: String,
        content: String
    ): PromptTemplate = withContext(dispatcher) {
        val now = Clock.System.now()
        val template = PromptTemplate(
            id = Uuid.random().toHexString(),
            name = name,
            content = content,
            createdAt = now,
            updatedAt = now
        )

        val db = Database(dbName)
        db.getCollection(COLLECTION_NAME, SCOPE_NAME)?.save(template.toDocument())
        db.close()
        template
    }

    override fun flowOfPromptTemplates(): Flow<List<PromptTemplate>> {
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
                        collection.getDocument(id)?.toPromptTemplate()
                    }
                } ?: emptyList()
            }
            .flowOn(dispatcher)
    }

    override suspend fun updatePromptTemplate(template: PromptTemplate): PromptTemplate = withContext(dispatcher) {
        val updatedTemplate = template.copy(updatedAt = Clock.System.now())
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
        collection?.save(updatedTemplate.toDocument())
        db.close()
        updatedTemplate
    }

    override suspend fun deletePromptTemplate(id: String) = withContext(dispatcher) {
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
        collection?.getDocument(id)?.let { document ->
            collection.delete(document)
        }
        db.close()
    }

    private fun initialize() {
        Database(dbName).createCollection(COLLECTION_NAME, SCOPE_NAME)
    }

    companion object {
        const val COLLECTION_NAME = "prompt_templates"
        const val SCOPE_NAME = "sync"

        fun create(
            dbName: String,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): PromptTemplateRepository {
            val repository = PromptTemplateRepository(dbName, dispatcher)
            repository.initialize()
            return repository
        }
    }
}