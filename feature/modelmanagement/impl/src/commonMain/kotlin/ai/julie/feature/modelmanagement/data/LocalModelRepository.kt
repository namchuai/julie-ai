package ai.julie.feature.modelmanagement.data

import ai.julie.core.model.aimodel.LocalModel
import ai.julie.feature.modelmanagement.data.mapper.toDocument
import ai.julie.feature.modelmanagement.data.mapper.toLocalModel
import ai.julie.feature.modelmanagement.domain.DeleteLocalModel
import ai.julie.feature.modelmanagement.domain.FlowOfLocalModels
import ai.julie.feature.modelmanagement.domain.StoreLocalModel
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

class LocalModelRepository private constructor(
    private val dbName: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : StoreLocalModel, DeleteLocalModel, FlowOfLocalModels {

    override suspend fun storeLocalModel(
        model: LocalModel
    ) = withContext(dispatcher) {
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)

        collection?.save(model.toDocument())
        db.close()
    }

    override suspend fun deleteLocalModel(modelId: String) = withContext(dispatcher) {
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
        collection?.getDocument(modelId)?.let { document ->
            collection.delete(document)
        }
        db.close()
    }

    override fun flowOfLocalModels(): Flow<List<LocalModel>> {
        val collection = Database(dbName).getCollection(COLLECTION_NAME, SCOPE_NAME)
            ?: return flowOf(emptyList())

        val query = QueryBuilder.select(SelectResult.expression(Meta.id))
            .from(DataSource.collection(collection))

        return query.queryChangeFlow()
            .map { change ->
                change.results?.allResults()?.mapNotNull { result ->
                    val docId = result.getString(0)
                    docId?.let { id ->
                        collection.getDocument(id)?.toLocalModel()
                    }
                } ?: emptyList()
            }
            .flowOn(dispatcher)
    }

    companion object {
        const val COLLECTION_NAME = "local_models"
        const val SCOPE_NAME = "no_sync"

        fun create(
            dbName: String,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): LocalModelRepository {
            Database(dbName).createCollection(COLLECTION_NAME, SCOPE_NAME)
            return LocalModelRepository(dbName, dispatcher)
        }
    }
}