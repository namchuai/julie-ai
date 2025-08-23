package ai.julie.core.domain.prompt

/**
 * Interface for handling prompt templates with variable substitution
 */
interface PromptTemplate {
    /**
     * Renders a template string with the provided context variables
     *
     * @param template The Jinja2 template string
     * @param context Map of variable names to their values
     * @return The rendered template string
     */
    suspend fun render(template: String, context: Map<String, Any> = emptyMap()): String

    /**
     * Validates a template string for syntax errors
     *
     * @param template The Jinja2 template string to validate
     * @return Result containing true if valid, or an error if invalid
     */
    suspend fun validate(template: String): Result<Boolean>

    /**
     * Extracts all variable names from a template
     *
     * @param template The Jinja2 template string
     * @return Set of variable names found in the template
     */
    suspend fun extractVariables(template: String): Set<String>
}

/**
 * Data class representing a prompt template with metadata
 */
data class PromptTemplateData(
    val id: String,
    val name: String,
    val description: String? = null,
    val template: String,
    val variables: List<PromptVariable> = emptyList(),
    val category: String? = null,
    val tags: List<String> = emptyList()
)

/**
 * Data class representing a template variable
 */
data class PromptVariable(
    val name: String,
    val description: String? = null,
    val type: VariableType = VariableType.STRING,
    val required: Boolean = true,
    val defaultValue: Any? = null,
    val examples: List<String> = emptyList()
)

/**
 * Enum for variable types
 */
enum class VariableType {
    STRING,
    NUMBER,
    BOOLEAN,
    LIST,
    OBJECT
}

/**
 * Common prompt template errors
 */
sealed class PromptTemplateError : Exception() {
    data class InvalidTemplate(
        override val message: String,
        override val cause: Throwable? = null
    ) : PromptTemplateError()

    data class MissingVariable(val variableName: String) : PromptTemplateError() {
        override val message: String = "Missing required variable: $variableName"
    }

    data class RenderError(override val message: String, override val cause: Throwable? = null) :
        PromptTemplateError()
}