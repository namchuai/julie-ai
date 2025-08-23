package ai.julie.feature.promptlab.data.repository

import ai.julie.feature.promptlab.model.PromptTemplate
import ai.julie.feature.promptlab.model.PromptVersion
import ai.julie.feature.promptlab.repository.PromptTemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class PromptTemplateRepositoryImpl(
    private val dbName: String
) : PromptTemplateRepository {
    private val templates = MutableStateFlow<Map<String, PromptTemplate>>(emptyMap())
    private val versions = MutableStateFlow<Map<String, PromptVersion>>(emptyMap())

    override suspend fun create(template: PromptTemplate): PromptTemplate {
        templates.update { it + (template.id to template) }
        return template
    }

    override suspend fun update(template: PromptTemplate): PromptTemplate {
        templates.update { it + (template.id to template) }
        return template
    }

    override suspend fun delete(id: String) {
        templates.update { it - id }
    }

    override suspend fun getById(id: String): PromptTemplate? {
        return templates.value[id]
    }

    override suspend fun getByProjectId(projectId: String): List<PromptTemplate> {
        return templates.value.values.filter { it.projectId == projectId }
    }

    override fun observeByProjectId(projectId: String): Flow<List<PromptTemplate>> {
        return templates.map { map ->
            map.values.filter { it.projectId == projectId }
        }
    }

    override fun observeById(id: String): Flow<PromptTemplate?> {
        return templates.map { it[id] }
    }

    // Version management
    override suspend fun createVersion(version: PromptVersion): PromptVersion {
        versions.update { it + (version.id to version) }
        return version
    }

    override suspend fun getVersions(templateId: String): List<PromptVersion> {
        return versions.value.values
            .filter { it.templateId == templateId }
            .sortedByDescending { it.versionNumber }
    }

    override suspend fun getVersion(versionId: String): PromptVersion? {
        return versions.value[versionId]
    }

    override fun observeVersions(templateId: String): Flow<List<PromptVersion>> {
        return versions.map { map ->
            map.values
                .filter { it.templateId == templateId }
                .sortedByDescending { it.versionNumber }
        }
    }
}