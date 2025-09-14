package ai.julie.feature.promptlab.repository

import ai.julie.feature.promptlab.model.TestSession
import kotlinx.coroutines.flow.Flow

interface TestSessionRepository {
    suspend fun create(session: TestSession): TestSession
    suspend fun update(session: TestSession): TestSession
    suspend fun delete(id: String)
    suspend fun getById(id: String): TestSession?
    suspend fun getByTemplateId(templateId: String): List<TestSession>
    suspend fun getByProjectId(projectId: String): List<TestSession>
    fun observeByTemplateId(templateId: String): Flow<List<TestSession>>
    fun observeById(id: String): Flow<TestSession?>
}