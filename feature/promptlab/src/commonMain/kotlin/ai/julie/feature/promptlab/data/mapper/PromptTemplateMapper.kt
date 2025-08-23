package ai.julie.feature.promptlab.data.mapper

import ai.julie.feature.promptlab.model.PromptTemplate
import kotbase.Document
import kotbase.MutableDocument
import kotlinx.datetime.Instant

fun PromptTemplate.toDocument(): MutableDocument = MutableDocument(id).apply {
    setString("id", id)
    setString("projectId", projectId)
    setString("name", name)
    setString("content", content)
    setDictionary("variables", variables.toMutableDictionary())
    setInt("currentVersion", currentVersion)
    setString("createdAt", createdAt.toString())
    setString("updatedAt", updatedAt.toString())
}

fun Document.toPromptTemplate(): PromptTemplate? {
    return try {
        PromptTemplate(
            id = getString("id") ?: return null,
            projectId = getString("projectId") ?: return null,
            name = getString("name") ?: return null,
            content = getString("content") ?: return null,
            variables = getDictionary("variables")?.toVariableConfigMap() ?: emptyMap(),
            currentVersion = getInt("currentVersion"),
            createdAt = Instant.parse(getString("createdAt") ?: return null),
            updatedAt = Instant.parse(getString("updatedAt") ?: return null)
        )
    } catch (e: Exception) {
        null
    }
}

