package ai.julie.core.domain.prompt

import ai.julie.logging.Logger

/**
 * Use case for rendering prompt templates with context
 */
class RenderPromptUseCase(
    private val promptTemplate: PromptTemplate,
    private val repository: PromptTemplateRepository
) {
    private val TAG = "RenderPromptUseCase"

    /**
     * Render a template by ID with the given context
     */
    suspend fun renderTemplateById(
        templateId: String,
        context: Map<String, Any>
    ): Result<String> {
        return try {
            val template = repository.getTemplate(templateId)
                ?: return Result.failure(
                    PromptTemplateError.InvalidTemplate("Template not found: $templateId")
                )

            renderTemplate(template, context)
        } catch (e: Exception) {
            Logger.e("[$TAG] Failed to render template $templateId: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Render a prompt template with context
     */
    suspend fun renderTemplate(
        template: PromptTemplateData,
        context: Map<String, Any>
    ): Result<String> {
        return try {
            // Validate required variables
            val missingVariables = template.variables
                .filter { it.required && it.name !in context }
                .map { it.name }

            if (missingVariables.isNotEmpty()) {
                return Result.failure(
                    PromptTemplateError.MissingVariable(
                        "Missing required variables: ${missingVariables.joinToString(", ")}"
                    )
                )
            }

            // Add default values for optional variables
            val enrichedContext = template.variables
                .filter { !it.required && it.defaultValue != null && it.name !in context }
                .associate { it.name to it.defaultValue!! }
                .plus(context)

            // Render the template
            val rendered = promptTemplate.render(template.template, enrichedContext)
            Result.success(rendered)

        } catch (e: PromptTemplateError) {
            Logger.e("[$TAG] Template error: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Logger.e("[$TAG] Unexpected error rendering template: ${e.message}")
            Result.failure(
                PromptTemplateError.RenderError(
                    "Failed to render template: ${e.message}",
                    e
                )
            )
        }
    }

    /**
     * Render a raw template string with context
     */
    suspend fun renderRawTemplate(
        templateString: String,
        context: Map<String, Any>
    ): Result<String> {
        return try {
            val rendered = promptTemplate.render(templateString, context)
            Result.success(rendered)
        } catch (e: Exception) {
            Logger.e("[$TAG] Failed to render raw template: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Validate a template and extract its variables
     */
    suspend fun analyzeTemplate(templateString: String): Result<TemplateAnalysis> {
        return try {
            val validationResult = promptTemplate.validate(templateString)
            if (validationResult.isFailure) {
                return Result.failure(validationResult.exceptionOrNull()!!)
            }

            val variables = promptTemplate.extractVariables(templateString)

            Result.success(
                TemplateAnalysis(
                    isValid = true,
                    variables = variables,
                    template = templateString
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Result of template analysis
 */
data class TemplateAnalysis(
    val isValid: Boolean,
    val variables: Set<String>,
    val template: String,
    val errors: List<String> = emptyList()
)