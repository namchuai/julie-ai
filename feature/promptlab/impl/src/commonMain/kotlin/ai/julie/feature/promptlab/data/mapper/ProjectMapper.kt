package ai.julie.feature.promptlab.data.mapper

import ai.julie.feature.promptlab.model.Project
import kotbase.Document
import kotbase.MutableArray
import kotbase.MutableDocument
import kotlinx.datetime.Instant

fun Project.toDocument(): MutableDocument = MutableDocument(id).apply {
    setString("id", id)
    setString("workspaceId", workspaceId)
    setString("name", name)
    description?.let { setString("description", it) }
    setArray("tags", MutableArray(tags))
    setString("createdAt", createdAt.toString())
    setString("updatedAt", updatedAt.toString())
}

fun Document.toProject(): Project? {
    return try {
        Project(
            id = getString("id") ?: return null,
            workspaceId = getString("workspaceId") ?: return null,
            name = getString("name") ?: return null,
            description = getString("description"),
            tags = getArray("tags")?.toList()?.mapNotNull { it as? String } ?: emptyList(),
            createdAt = Instant.parse(getString("createdAt") ?: return null),
            updatedAt = Instant.parse(getString("updatedAt") ?: return null)
        )
    } catch (e: Exception) {
        null
    }
}