package ai.julie.core.domain.prompt

import com.hubspot.jinjava.Jinjava
import com.hubspot.jinjava.JinjavaConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

/**
 * Jinjava implementation of PromptTemplate for JVM/Desktop platforms
 */
class JinjavaPromptTemplate : PromptTemplate {

    private val jinjava: Jinjava

    init {
        // Configure Jinjava with safe defaults
        val config = JinjavaConfig.newBuilder()
            .withMaxRenderDepth(10) // Prevent infinite recursion
            .withEnableRecursiveMacroCalls(false)
            .withFailOnUnknownTokens(true) // Fail fast on unknown variables
            .build()

        jinjava = Jinjava(config)
    }

    override suspend fun render(
        template: String,
        context: Map<String, Any>
    ): String = withContext(Dispatchers.IO) {
        try {
            // Convert Kotlin types to Java types for Jinjava
            val javaContext = context.mapValues { (_, value) ->
                when (value) {
                    is List<*> -> value.toList()
                    is Map<*, *> -> value.toMap()
                    else -> value
                }
            }

            val result = jinjava.render(template, javaContext)

            // Check for errors
            val interpreter = jinjava.newInterpreter()
            val errors = interpreter.errors

            if (errors.isNotEmpty()) {
                val errorMessages = errors.joinToString("; ") { it.message }
                throw PromptTemplateError.RenderError(
                    "Template rendering errors: $errorMessages"
                )
            }

            result
        } catch (e: PromptTemplateError) {
            throw e
        } catch (e: Exception) {
            throw PromptTemplateError.RenderError(
                "Failed to render template: ${e.message}",
                e
            )
        }
    }

    override suspend fun validate(template: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Try to parse the template
            val interpreter = jinjava.newInterpreter()
            jinjava.render(template, emptyMap<String, Any>())

            // Check for syntax errors
            val errors = interpreter.errors.filter { error ->
                error.message.contains("syntax", ignoreCase = true) ||
                        error.message.contains("parse", ignoreCase = true)
            }

            if (errors.isNotEmpty()) {
                val errorMessage = errors.joinToString("; ") { it.message }
                Result.failure(
                    PromptTemplateError.InvalidTemplate(
                        "Template syntax errors: $errorMessage"
                    )
                )
            } else {
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(
                PromptTemplateError.InvalidTemplate(
                    "Invalid template: ${e.message}",
                    e
                )
            )
        }
    }

    override suspend fun extractVariables(template: String): Set<String> =
        withContext(Dispatchers.IO) {
            val variables = mutableSetOf<String>()

            // Pattern to match Jinja2 variables: {{ variable_name }}
            val variablePattern =
                Pattern.compile("\\{\\{\\s*(\\w+)(?:\\.[\\w\\[\\]\"']+)*\\s*\\}\\}")
            val matcher = variablePattern.matcher(template)

            while (matcher.find()) {
                matcher.group(1)?.let { variables.add(it) }
            }

            // Pattern to match variables in control structures: {% for item in items %}
            val controlPattern = Pattern.compile("\\{%.*?\\b(\\w+)\\b.*?%\\}")
            val controlMatcher = controlPattern.matcher(template)

            while (controlMatcher.find()) {
                // Extract variables from for loops, if statements, etc.
                val controlBlock = controlMatcher.group()

                // Extract from for loops: {% for x in collection %}
                val forPattern = Pattern.compile("\\bfor\\s+(\\w+)\\s+in\\s+(\\w+)")
                val forMatcher = forPattern.matcher(controlBlock)
                while (forMatcher.find()) {
                    forMatcher.group(2)?.let { variables.add(it) }
                }

                // Extract from if statements: {% if variable %}
                val ifPattern = Pattern.compile("\\bif\\s+(\\w+)(?:\\s|\\.|\\[|\\})")
                val ifMatcher = ifPattern.matcher(controlBlock)
                while (ifMatcher.find()) {
                    ifMatcher.group(1)?.let { variables.add(it) }
                }
            }

            variables
        }

    companion object {
        /**
         * Creates a new instance of JinjavaPromptTemplate
         */
        fun create(): PromptTemplate = JinjavaPromptTemplate()
    }
}

/**
 * Extension function to render a template string directly
 */
suspend fun String.renderTemplate(context: Map<String, Any> = emptyMap()): String {
    return JinjavaPromptTemplate().render(this, context)
}