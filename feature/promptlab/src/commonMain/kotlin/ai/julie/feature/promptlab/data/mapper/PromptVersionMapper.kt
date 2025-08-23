package ai.julie.feature.promptlab.data.mapper

import ai.julie.feature.promptlab.model.PromptVersion
import ai.julie.feature.promptlab.model.VariableConfig
import ai.julie.feature.promptlab.model.VariableType
import kotbase.Dictionary
import kotbase.Document
import kotbase.MutableArray
import kotbase.MutableDictionary
import kotbase.MutableDocument
import kotlinx.datetime.Instant

fun PromptVersion.toDocument(): MutableDocument = MutableDocument(id).apply {
    setString("id", id)
    setString("templateId", templateId)
    setInt("versionNumber", versionNumber)
    setString("content", content)
    setDictionary("variables", variables.toMutableDictionary())
    setString("commitMessage", commitMessage)
    setString("authorId", authorId)
    setString("createdAt", createdAt.toString())
}

fun Document.toPromptVersion(): PromptVersion? {
    return try {
        PromptVersion(
            id = getString("id") ?: return null,
            templateId = getString("templateId") ?: return null,
            versionNumber = getInt("versionNumber"),
            content = getString("content") ?: return null,
            variables = getDictionary("variables")?.toVariableConfigMap() ?: emptyMap(),
            commitMessage = getString("commitMessage") ?: return null,
            authorId = getString("authorId") ?: return null,
            createdAt = Instant.parse(getString("createdAt") ?: return null)
        )
    } catch (e: Exception) {
        null
    }
}

fun Map<String, VariableConfig>.toMutableDictionary(): MutableDictionary {
    val dict = MutableDictionary()
    forEach { (key, config) ->
        val configDict = MutableDictionary().apply {
            setString("type", config.type.name)
            config.description?.let { setString("description", it) }
            config.defaultValue?.let { setString("defaultValue", it) }
            config.options?.let { setArray("options", MutableArray(it)) }
        }
        dict.setDictionary(key, configDict)
    }
    return dict
}

fun Dictionary.toVariableConfigMap(): Map<String, VariableConfig> {
    val map = mutableMapOf<String, VariableConfig>()
    keys.forEach { key ->
        getDictionary(key)?.let { configDict ->
            val type =
                configDict.getString("type")?.let { VariableType.valueOf(it) } ?: return@forEach
            map[key] = VariableConfig(
                type = type,
                description = configDict.getString("description"),
                defaultValue = configDict.getString("defaultValue"),
                options = configDict.getArray("options")?.toList()?.mapNotNull { it as? String }
            )
        }
    }
    return map
}