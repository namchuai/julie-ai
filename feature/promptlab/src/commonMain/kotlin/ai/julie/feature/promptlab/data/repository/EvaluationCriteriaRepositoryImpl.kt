package ai.julie.feature.promptlab.data.repository

import ai.julie.feature.promptlab.model.EvaluationCriteria
import ai.julie.feature.promptlab.repository.EvaluationCriteriaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class EvaluationCriteriaRepositoryImpl(
    private val dbName: String
) : EvaluationCriteriaRepository {
    private val criteria = MutableStateFlow<Map<String, EvaluationCriteria>>(emptyMap())

    override suspend fun create(criteria: EvaluationCriteria): EvaluationCriteria {
        this.criteria.update { it + (criteria.id to criteria) }
        return criteria
    }

    override suspend fun update(criteria: EvaluationCriteria): EvaluationCriteria {
        this.criteria.update { it + (criteria.id to criteria) }
        return criteria
    }

    override suspend fun delete(id: String) {
        criteria.update { it - id }
    }

    override suspend fun getById(id: String): EvaluationCriteria? {
        return criteria.value[id]
    }

    override suspend fun getByProjectId(projectId: String): List<EvaluationCriteria> {
        return criteria.value.values.filter { it.projectId == projectId }
    }

    override fun observeByProjectId(projectId: String): Flow<List<EvaluationCriteria>> {
        return criteria.map { map ->
            map.values.filter { it.projectId == projectId }
        }
    }

    override fun observeById(id: String): Flow<EvaluationCriteria?> {
        return criteria.map { it[id] }
    }
}