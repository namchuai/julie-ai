package ai.julie.feature.promptlab.repository

import ai.julie.feature.promptlab.model.Workspace
import kotlinx.coroutines.flow.Flow

interface WorkspaceRepository {
    suspend fun create(workspace: Workspace): Workspace
    suspend fun update(workspace: Workspace): Workspace
    suspend fun delete(id: String)
    suspend fun getById(id: String): Workspace?
    fun observeAll(): Flow<List<Workspace>>
    fun observeById(id: String): Flow<Workspace?>
}