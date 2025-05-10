package ai.julie.feature.message.data

import ai.julie.feature.message.domain.CreateMessage
import ai.julie.feature.message.domain.DeleteMessage
import ai.julie.feature.message.domain.FlowOfMessages
import ai.julie.feature.message.domain.UpdateMessage
import ai.julie.feature.message.domain.model.EnrichedMessage
import ai.julie.feature.message.domain.model.EnrichedRole
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageId
import com.aallam.openai.api.message.TextContent
import com.aallam.openai.api.thread.ThreadId
import kotbase.DataSource
import kotbase.Database
import kotbase.Expression
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

class MessageRepository private constructor(
    private val dbName: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CreateMessage, FlowOfMessages, DeleteMessage, UpdateMessage {

    override suspend fun createMessage(
        threadId: String,
        content: String,
        role: EnrichedRole
    ): EnrichedMessage {
        val enrichedMessage = EnrichedMessage(
            message = Message(
                id = MessageId(Uuid.random().toHexString()),
                createdAt = Clock.System.now().epochSeconds.toInt(),
                threadId = ThreadId(threadId),
                role = role.role,
                content = listOf(
                    MessageContent.Text(
                        text = TextContent(
                            value = content,
                            annotations = emptyList()
                        )
                    )
                ),
                metadata = emptyMap(),
            )
        )

        return withContext(dispatcher) {
            val db = Database(dbName)
            db.getCollection(COLLECTION_NAME, SCOPE_NAME)?.save(enrichedMessage.toDocument())
            db.close()
            enrichedMessage
        }
    }

    override fun flowOfMessages(threadId: String): Flow<List<EnrichedMessage>> {
        val collection = Database(dbName).getCollection(COLLECTION_NAME, SCOPE_NAME)
            ?: return flowOf(emptyList())

        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
            .where(Expression.property("threadId").equalTo(Expression.string(threadId)))
            .orderBy(Ordering.property("createdAt"))

        return query.queryChangeFlow()
            .map { change ->
                val results = change.results?.allResults()?.mapNotNull { result ->
                    result.toMap().values.firstOrNull()?.let { docData ->
                        if (docData is Map<*, *>) {
                            @Suppress("UNCHECKED_CAST")
                            collection.getDocument(docData["id"] as? String ?: "")
                                ?.toEnrichedMessage()
                        } else null
                    }
                } ?: emptyList()
                results
            }
            .flowOn(dispatcher)
    }

    override suspend fun deleteMessage(messageId: String) = withContext(dispatcher) {
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
        collection?.getDocument(messageId)?.let { document ->
            collection.delete(document)
        }
        db.close()
    }

    override suspend fun updateMessage(
        messageId: String,
        content: String
    ) = withContext(dispatcher) {
        // Get existing message first
        val db = Database(dbName)
        val collection = db.getCollection(COLLECTION_NAME, SCOPE_NAME)
        val document = collection?.getDocument(messageId)
        val existingMessage = document?.toEnrichedMessage()
            ?: throw IllegalArgumentException("Message with ID $messageId not found")

        // Create updated message with new content
        val updatedMessage = EnrichedMessage(
            message = Message(
                id = existingMessage.message.id,
                createdAt = existingMessage.message.createdAt,
                threadId = existingMessage.message.threadId,
                role = existingMessage.message.role,
                content = listOf(
                    MessageContent.Text(
                        text = TextContent(
                            value = content,
                            annotations = emptyList()
                        )
                    )
                ),
                metadata = existingMessage.message.metadata,
                assistantId = existingMessage.message.assistantId,
                runId = existingMessage.message.runId
            )
        )

        // Save updated message
        collection.save(updatedMessage.toDocument())
//        db.close()
        updatedMessage
    }

    private fun initializeMessages() {
        Database(dbName).createCollection(COLLECTION_NAME, SCOPE_NAME)
    }

    companion object {
        const val COLLECTION_NAME = "messages"
        const val SCOPE_NAME = "sync"

        fun create(
            dbName: String,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): MessageRepository {
            val repository = MessageRepository(dbName, dispatcher)
            repository.initializeMessages()
            return repository
        }
    }
}