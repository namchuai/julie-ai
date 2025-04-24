package ai.julie.storage.kotbase

import ai.julie.storage.MessageOperations

class MessageOperationsImpl(
    private val dbName: String,
) : MessageOperations {

    companion object {
        const val COLLECTION_NAME = "messages"
        const val SCOPE_NAME = "sync"
    }
}