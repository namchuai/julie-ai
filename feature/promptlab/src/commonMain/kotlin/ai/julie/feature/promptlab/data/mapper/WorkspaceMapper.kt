package ai.julie.feature.promptlab.data.mapper

import ai.julie.feature.promptlab.model.Workspace
import ai.julie.feature.promptlab.model.WorkspaceType
import kotbase.Document
import kotbase.MutableDocument
import kotlinx.datetime.Instant

fun Workspace.toDocument(): MutableDocument = MutableDocument(id).apply {
    setString("id", id)
    setString("name", name)
    setString("type", type.name)
    setString("owner", owner)
    setString("createdAt", createdAt.toString())
    setString("updatedAt", updatedAt.toString())
}

fun Document.toWorkspace(): Workspace? {
    return try {
        Workspace(
            id = getString("id") ?: return null,
            name = getString("name") ?: return null,
            type = WorkspaceType.valueOf(getString("type") ?: return null),
            owner = getString("owner") ?: return null,
            createdAt = Instant.parse(getString("createdAt") ?: return null),
            updatedAt = Instant.parse(getString("updatedAt") ?: return null)
        )
    } catch (e: Exception) {
        null
    }
}