package ai.julie.feature.promptlab.repository

import ai.julie.feature.promptlab.model.PromptTemplate
import ai.julie.feature.promptlab.model.PromptVersion
import kotlinx.coroutines.flow.Flow

interface PromptTemplateRepository {
    suspend fun create(template: PromptTemplate): PromptTemplate
    suspend fun update(template: PromptTemplate): PromptTemplate
    suspend fun delete(id: String)
    suspend fun getById(id: String): PromptTemplate?
    suspend fun getByProjectId(projectId: String): List<PromptTemplate>
    fun observeByProjectId(projectId: String): Flow<List<PromptTemplate>>
    fun observeById(id: String): Flow<PromptTemplate?>

    // Version management
    suspend fun createVersion(version: PromptVersion): PromptVersion
    suspend fun getVersions(templateId: String): List<PromptVersion>
    suspend fun getVersion(versionId: String): PromptVersion?
    fun observeVersions(templateId: String): Flow<List<PromptVersion>>
}