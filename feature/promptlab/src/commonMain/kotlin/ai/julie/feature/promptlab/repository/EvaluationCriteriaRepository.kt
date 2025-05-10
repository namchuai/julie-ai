package ai.julie.feature.promptlab.repository

import ai.julie.feature.promptlab.model.EvaluationCriteria
import kotlinx.coroutines.flow.Flow

interface EvaluationCriteriaRepository {
    suspend fun create(criteria: EvaluationCriteria): EvaluationCriteria
    suspend fun update(criteria: EvaluationCriteria): EvaluationCriteria
    suspend fun delete(id: String)
    suspend fun getById(id: String): EvaluationCriteria?
    suspend fun getByProjectId(projectId: String): List<EvaluationCriteria>
    fun observeByProjectId(projectId: String): Flow<List<EvaluationCriteria>>
    fun observeById(id: String): Flow<EvaluationCriteria?>
}