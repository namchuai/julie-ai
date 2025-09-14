package ai.julie.feature.promptlab.repository

import ai.julie.feature.promptlab.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    suspend fun create(project: Project): Project
    suspend fun update(project: Project): Project
    suspend fun delete(id: String)
    suspend fun getById(id: String): Project?
    suspend fun getByWorkspaceId(workspaceId: String): List<Project>
    fun observeByWorkspaceId(workspaceId: String): Flow<List<Project>>
    fun observeById(id: String): Flow<Project?>
}