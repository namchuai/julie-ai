package ai.julie.feature.modelconfig.data

import ai.julie.feature.modelconfig.domain.DeleteModelMetadata
import ai.julie.feature.modelconfig.domain.FlowOfModelMetadata
import ai.julie.feature.modelconfig.domain.StoreModelMetadata
import ai.julie.feature.modelconfig.domain.gguf.GgufMetadata
import ai.julie.feature.modelconfig.domain.gguf.GgufMetadataFactory
import ai.julie.logging.Logger
import kotbase.Database
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ModelMetadataRepository private constructor(
    private val dbName: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : StoreModelMetadata, FlowOfModelMetadata, DeleteModelMetadata {

    private val TAG = "ModelMetadataRepository"

    override suspend fun storeModelMetadata(modelId: String, metadata: GgufMetadata) = withContext(dispatcher) {
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)

        collection?.save(metadata.toDocument(modelId))
        db.close()

        Logger.d("[$TAG] Successfully stored model metadata: $modelId")
    }

    override fun flowOfModelMetadata(modelId: String): Flow<GgufMetadata> {
        return flow {
            val db = Database(dbName)
            val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
            require(collection != null) {
                "Collection $COLLECTION_NAME in scope $SCOPE_NAME does not exist in database $dbName"
            }

            val document = collection.getDocument(modelId)
                ?: throw IllegalStateException("Model metadata not found for modelId: $modelId")
            
            val metadata = GgufMetadataFactory.fromDocument(document)

            emit(metadata)
            db.close()
        }.flowOn(dispatcher)
    }

    override suspend fun deleteModelMetadata(modelId: String) = withContext(dispatcher) {
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
        collection?.getDocument(modelId)?.let { document ->
            collection.delete(document)
        }
        db.close()
        Logger.d("[$TAG] Successfully deleted model metadata: $modelId")
    }

    companion object {
        const val COLLECTION_NAME = "ai_model_metadata"
        const val SCOPE_NAME = "no_sync"

        fun create(
            dbName: String,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): ModelMetadataRepository {
            Database(dbName).createCollection(COLLECTION_NAME, SCOPE_NAME)
            return ModelMetadataRepository(dbName, dispatcher)
        }
    }
}