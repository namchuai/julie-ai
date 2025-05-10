package ai.julie.feature.modelmanagement.data.mapper

import ai.julie.core.model.aimodel.LocalModel
import kotbase.Document
import kotbase.MutableDocument
import kotlinx.datetime.Clock

fun LocalModel.toDocument(): MutableDocument {
    val currentTime = Clock.System.now().toString()
    
    return MutableDocument(id).apply {
        setString("id", this@toDocument.id)
        setString("title", this@toDocument.title)
        setString("description", this@toDocument.description)
        setString("localPath", this@toDocument.localPath ?: "")
        setString("createdAt", currentTime)
        setString("updatedAt", currentTime)
    }
}

fun Document.toLocalModel(): LocalModel {
    return LocalModel(
        id = getString("id") ?: this.id,
        title = getString("title") ?: "",
        description = getString("description") ?: "",
        localPath = getString("localPath")
    )
}