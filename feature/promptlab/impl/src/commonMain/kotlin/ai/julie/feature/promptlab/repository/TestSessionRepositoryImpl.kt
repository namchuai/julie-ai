package ai.julie.feature.promptlab.data.repository

import ai.julie.feature.promptlab.model.TestSession
import ai.julie.feature.promptlab.repository.TestSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestSessionRepositoryImpl(
    private val dbName: String
) : TestSessionRepository {
    private val sessions = MutableStateFlow<Map<String, TestSession>>(emptyMap())

    override suspend fun create(session: TestSession): TestSession {
        sessions.update { it + (session.id to session) }
        return session
    }

    override suspend fun update(session: TestSession): TestSession {
        sessions.update { it + (session.id to session) }
        return session
    }

    override suspend fun delete(id: String) {
        sessions.update { it - id }
    }

    override suspend fun getById(id: String): TestSession? {
        return sessions.value[id]
    }

    override suspend fun getByTemplateId(templateId: String): List<TestSession> {
        return sessions.value.values
            .filter { it.templateId == templateId }
            .sortedByDescending { it.createdAt }
    }

    override suspend fun getByProjectId(projectId: String): List<TestSession> {
        return sessions.value.values
            .filter { session ->
                // We would need to join with templates to get projectId
                // For now, returning empty list
                false
            }
            .sortedByDescending { it.createdAt }
    }

    override fun observeByTemplateId(templateId: String): Flow<List<TestSession>> {
        return sessions.map { map ->
            map.values
                .filter { it.templateId == templateId }
                .sortedByDescending { it.createdAt }
        }
    }

    override fun observeById(id: String): Flow<TestSession?> {
        return sessions.map { it[id] }
    }
}