package ai.julie.feature.appsetting.data

import ai.julie.feature.appsetting.domain.AppSetting
import ai.julie.feature.appsetting.domain.FlowOfAppSetting
import ai.julie.feature.appsetting.domain.UpdateAppSetting
import ai.julie.logging.Logger
import kotbase.Database
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class AppSettingRepository private constructor(
    private val dbName: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : FlowOfAppSetting, UpdateAppSetting {

    private val TAG = "AppSettingRepository"

    override fun flowOfAppSetting(): Flow<AppSetting> = flow {
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)

        val appSetting = collection?.getDocument(DOCUMENT_ID)?.toAppSetting() ?: AppSetting()

        db.close()
        emit(appSetting)
    }.flowOn(dispatcher)

    override suspend fun updateAppSetting(setting: AppSetting) = withContext(dispatcher) {
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)

        val document = setting.toDocument(DOCUMENT_ID)
        collection?.save(document)
        db.close()
        Logger.d("[$TAG] App settings updated successfully")
    }

    private fun initializeSettings() {
        Database(dbName).createCollection(COLLECTION_NAME, SCOPE_NAME)
    }

    companion object {
        const val COLLECTION_NAME = "app_settings"
        const val SCOPE_NAME = "no_sync"
        const val DOCUMENT_ID = "main_settings"

        fun create(
            dbName: String,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): AppSettingRepository {
            val repository = AppSettingRepository(dbName, dispatcher)
            repository.initializeSettings()
            return repository
        }
    }
}
