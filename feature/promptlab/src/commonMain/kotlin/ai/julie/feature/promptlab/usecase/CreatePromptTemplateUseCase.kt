package ai.julie.feature.promptlab.usecase

import ai.julie.feature.promptlab.model.PromptTemplate
import ai.julie.feature.promptlab.model.PromptVersion
import ai.julie.feature.promptlab.model.VariableConfig
import ai.julie.feature.promptlab.repository.PromptTemplateRepository
import kotlinx.datetime.Clock
import kotlin.uuid.Uuid

class CreatePromptTemplateUseCase(
    private val promptTemplateRepository: PromptTemplateRepository
) {
    suspend operator fun invoke(
        projectId: String,
        name: String,
        content: String,
        variables: Map<String, VariableConfig> = emptyMap(),
        commitMessage: String = "Initial version",
        authorId: String = "default_user" // TODO: Get from auth
    ): Result<PromptTemplate> = runCatching {
        val now = Clock.System.now()
        val templateId = Uuid.random().toHexString()

        // Create the template
        val template = PromptTemplate(
            id = templateId,
            projectId = projectId,
            name = name,
            content = content,
            variables = variables,
            currentVersion = 1,
            createdAt = now,
            updatedAt = now
        )

        // Create initial version
        val version = PromptVersion(
            id = Uuid.random().toHexString(),
            templateId = templateId,
            versionNumber = 1,
            content = content,
            variables = variables,
            commitMessage = commitMessage,
            authorId = authorId,
            createdAt = now
        )

        // Save both
        promptTemplateRepository.create(template)
        promptTemplateRepository.createVersion(version)

        template
    }
}