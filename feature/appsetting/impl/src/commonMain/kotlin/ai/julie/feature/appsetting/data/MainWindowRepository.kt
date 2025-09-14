package ai.julie.feature.appsetting.data

import ai.julie.feature.appsetting.domain.GetMainWindowSetting
import ai.julie.feature.appsetting.domain.MainWindowSetting
import ai.julie.feature.appsetting.domain.UpdateMainWindowSetting
import kotbase.Database
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class MainWindowRepository private constructor(
    private val dbName: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : GetMainWindowSetting, UpdateMainWindowSetting {

    override suspend fun updateMainWindowSetting(setting: MainWindowSetting) =
        withContext(dispatcher) {
            try {
                val db = Database(dbName)
                val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
                val document = setting.toDocument(DOCUMENT_ID)
                collection?.save(document)
                db.close()
            } catch (e: Exception) {
                println("Warning: Failed to save window settings: ${e.message}")
            }
        }

    override suspend fun getMainWindowSetting(): MainWindowSetting = withContext(dispatcher) {
        try {
            val db = Database(dbName)
            val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
            val result = collection?.getDocument(DOCUMENT_ID)?.toMainWindowSetting() ?: MainWindowSetting()
            db.close()
            result
        } catch (e: Exception) {
            println("Warning: Failed to load window settings: ${e.message}")
            MainWindowSetting()
        }
    }

    private fun initializeSettings() {
        try {
            val db = Database(dbName)
            try {
                db.createCollection(COLLECTION_NAME, SCOPE_NAME)
                db.close()
            } catch (e: Exception) {
                // Collection might already exist, that's okay
                db.close()
            }
        } catch (e: Exception) {
            println("Warning: Database initialization failed: ${e.message}")
        }
    }

    companion object {
        const val COLLECTION_NAME = "main_window_settings"
        const val SCOPE_NAME = "no_sync"
        const val DOCUMENT_ID = "main_window"

        fun create(
            dbName: String,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): MainWindowRepository {
            val repository = MainWindowRepository(dbName, dispatcher)
            repository.initializeSettings()
            return repository
        }
    }
}
