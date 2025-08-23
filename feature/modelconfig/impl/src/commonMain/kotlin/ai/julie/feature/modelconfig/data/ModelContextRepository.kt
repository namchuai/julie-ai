package ai.julie.feature.modelconfig.data

import ai.julie.core.model.ModelContextParams
import ai.julie.feature.modelconfig.data.mapper.toDocument
import ai.julie.feature.modelconfig.data.mapper.toModelContextParams
import ai.julie.feature.modelconfig.domain.DeleteModelContextParam
import ai.julie.feature.modelconfig.domain.FlowOfModelContextParam
import ai.julie.feature.modelconfig.domain.StoreModelContextParam
import ai.julie.logging.Logger
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ModelContextRepository private constructor(
    private val dbName: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FlowOfModelContextParam,
    StoreModelContextParam,
    DeleteModelContextParam {

    private val TAG = "ModelContextRepository"

    override suspend fun storeModelContextParam(
        modelId: String,
        modelContextParams: ModelContextParams,
    ) = withContext(dispatcher) {
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)

        collection?.save(modelContextParams.toDocument(modelId))
        db.close()

        Logger.d("[$TAG] Successfully stored model context params: $modelId")
    }

    override fun flowOfModelContextParam(modelId: String): Flow<ModelContextParams> {
        val collection = Database(dbName).getCollection(COLLECTION_NAME, SCOPE_NAME)
        require(collection != null) {
            "Collection $COLLECTION_NAME in scope $SCOPE_NAME does not exist in database $dbName"
        }

        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
            .where(expression = Meta.id.equalTo(Expression.string(value = modelId)))

        return query.queryChangeFlow()
            .map { change ->
                val result =
                    change.results?.allResults()?.firstOrNull()?.toMap()?.values?.firstOrNull()
                Logger.d("[$TAG] Query result: $result")
                if (result is Map<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    collection.getDocument(result["id"] as? String ?: modelId)
                        ?.toModelContextParams() ?: ModelContextParams()
                } else {
                    ModelContextParams()
                }
            }
            .flowOn(dispatcher)
    }

    override suspend fun deleteModelContextParam(modelId: String) = withContext(dispatcher) {
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
        collection?.getDocument(modelId)?.let { document ->
            collection.delete(document)
        }
        db.close()
        Logger.d("[$TAG] Successfully deleted model context params: $modelId")
    }

    companion object {
        const val COLLECTION_NAME = "model_contexts"
        const val SCOPE_NAME = "no_sync"

        fun create(
            dbName: String,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): ModelContextRepository {
            Database(dbName).createCollection(COLLECTION_NAME, SCOPE_NAME)
            return ModelContextRepository(dbName, dispatcher)
        }
    }
}