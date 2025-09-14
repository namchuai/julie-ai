package ai.julie.feature.promptlab.data

import ai.julie.feature.promptlab.model.PromptTemplate
import kotbase.Document
import kotbase.MutableDocument
import kotlinx.datetime.Instant

fun PromptTemplate.toDocument(): MutableDocument {
    return MutableDocument(id).apply {
        setString("name", name)
        setString("content", content)
        setLong("created_at", createdAt.epochSeconds)
        setLong("updated_at", updatedAt.epochSeconds)
    }
}

fun Document.toPromptTemplate(): PromptTemplate? {
    return try {
        PromptTemplate(
            id = id,
            name = getString("name") ?: return null,
            content = getString("content") ?: return null,
            createdAt = Instant.fromEpochSeconds(getLong("created_at")),
            updatedAt = Instant.fromEpochSeconds(getLong("updated_at"))
        )
    } catch (e: Exception) {
        null
    }
}