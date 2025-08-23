package ai.julie.feature.appsetting.data

import ai.julie.feature.appsetting.domain.MainWindowSetting
import kotbase.Document
import kotbase.MutableDocument

fun MainWindowSetting.toDocument(documentId: String): MutableDocument {
    return MutableDocument(documentId).apply {
        setInt("x", x)
        setInt("y", y)
        setInt("width", width)
        setInt("height", height)
    }
}

fun Document.toMainWindowSetting(): MainWindowSetting {
    return MainWindowSetting(
        x = getInt("x"),
        y = getInt("y"),
        width = getInt("width"),
        height = getInt("height")
    )
}