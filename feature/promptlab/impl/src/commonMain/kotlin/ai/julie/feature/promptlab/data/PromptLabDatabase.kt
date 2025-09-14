package ai.julie.feature.promptlab.data

import ai.julie.feature.promptlab.repository.EvaluationCriteriaRepository
import ai.julie.feature.promptlab.repository.ProjectRepository
import ai.julie.feature.promptlab.repository.TestSessionRepository
import ai.julie.feature.promptlab.repository.WorkspaceRepository

interface PromptLabDatabase {
    val workspaceRepository: WorkspaceRepository
    val projectRepository: ProjectRepository
    val promptTemplateRepository: PromptTemplateRepository
    val testSessionRepository: TestSessionRepository
    val evaluationCriteriaRepository: EvaluationCriteriaRepository
}