package ai.julie.feature.appsetting.data

import ai.julie.feature.appsetting.domain.AppSetting
import kotbase.Document
import kotbase.MutableDocument

fun AppSetting.toDocument(documentId: String): MutableDocument {
    return MutableDocument(documentId).apply {
        setString("placeholder", placeholder)
    }
}

fun Document.toAppSetting(): AppSetting {
    return AppSetting(
        placeholder = getString("placeholder") ?: "empty"
    )
}