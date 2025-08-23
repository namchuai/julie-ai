package ai.julie.core.domain.prompt

/**
 * Collection of default prompt templates for common use cases
 */
object DefaultPrompts {

    val CHAT_ASSISTANT = PromptTemplateData(
        id = "chat_assistant",
        name = "Chat Assistant",
        description = "General purpose chat assistant prompt",
        template = """
            You are a helpful, harmless, and honest assistant. You should be helpful to the user, provide accurate information, and follow their instructions carefully.
            
            {% if system_context %}
            System Context: {{ system_context }}
            {% endif %}
            
            {% if user_name %}
            User: {{ user_name }}
            {% endif %}
            
            {{ user_message }}
        """.trimIndent(),
        variables = listOf(
            PromptVariable(
                name = "user_message",
                description = "The user's message",
                type = VariableType.STRING,
                required = true,
                examples = listOf("Hello, how can you help me today?")
            ),
            PromptVariable(
                name = "system_context",
                description = "Additional system context",
                type = VariableType.STRING,
                required = false,
                examples = listOf("You are helping with coding tasks")
            ),
            PromptVariable(
                name = "user_name",
                description = "The user's name",
                type = VariableType.STRING,
                required = false,
                examples = listOf("Alice", "Bob")
            )
        ),
        category = "general",
        tags = listOf("chat", "assistant", "general")
    )

    val CODE_ASSISTANT = PromptTemplateData(
        id = "code_assistant",
        name = "Code Assistant",
        description = "Specialized assistant for coding tasks",
        template = """
            You are an expert software developer and coding assistant. You provide accurate, well-structured code solutions and explanations.
            
            Programming Language: {{ language }}
            
            {% if task_type %}
            Task Type: {{ task_type }}
            {% endif %}
            
            {% if context %}
            Context: {{ context }}
            {% endif %}
            
            Request: {{ request }}
            
            Please provide:
            1. A clear solution
            2. Code examples if applicable
            3. Brief explanation of the approach
            
            {% if include_tests %}
            4. Include unit tests for the solution
            {% endif %}
        """.trimIndent(),
        variables = listOf(
            PromptVariable(
                name = "language",
                description = "Programming language",
                type = VariableType.STRING,
                required = true,
                examples = listOf("Python", "JavaScript", "Kotlin", "Java")
            ),
            PromptVariable(
                name = "request",
                description = "The coding request or problem",
                type = VariableType.STRING,
                required = true,
                examples = listOf("Create a function to sort an array", "Debug this code snippet")
            ),
            PromptVariable(
                name = "task_type",
                description = "Type of coding task",
                type = VariableType.STRING,
                required = false,
                examples = listOf("debugging", "optimization", "implementation", "review")
            ),
            PromptVariable(
                name = "context",
                description = "Additional context about the project",
                type = VariableType.STRING,
                required = false
            ),
            PromptVariable(
                name = "include_tests",
                description = "Whether to include unit tests",
                type = VariableType.BOOLEAN,
                required = false,
                defaultValue = false
            )
        ),
        category = "coding",
        tags = listOf("code", "programming", "development")
    )

    val CREATIVE_WRITING = PromptTemplateData(
        id = "creative_writing",
        name = "Creative Writing Assistant",
        description = "Assistant for creative writing tasks",
        template = """
            You are a creative writing assistant. Help the user with their writing project by providing engaging, creative content.
            
            Writing Type: {{ writing_type }}
            {% if genre %}
            Genre: {{ genre }}
            {% endif %}
            {% if tone %}
            Tone: {{ tone }}
            {% endif %}
            {% if target_audience %}
            Target Audience: {{ target_audience }}
            {% endif %}
            
            Writing Prompt: {{ prompt }}
            
            {% if additional_requirements %}
            Additional Requirements:
            {% for requirement in additional_requirements %}
            - {{ requirement }}
            {% endfor %}
            {% endif %}
        """.trimIndent(),
        variables = listOf(
            PromptVariable(
                name = "writing_type",
                description = "Type of writing",
                type = VariableType.STRING,
                required = true,
                examples = listOf("story", "poem", "article", "script", "essay")
            ),
            PromptVariable(
                name = "prompt",
                description = "The writing prompt or request",
                type = VariableType.STRING,
                required = true,
                examples = listOf("Write a short story about a time traveler")
            ),
            PromptVariable(
                name = "genre",
                description = "Writing genre",
                type = VariableType.STRING,
                required = false,
                examples = listOf("science fiction", "fantasy", "mystery", "romance")
            ),
            PromptVariable(
                name = "tone",
                description = "Writing tone",
                type = VariableType.STRING,
                required = false,
                examples = listOf("humorous", "serious", "dramatic", "lighthearted")
            ),
            PromptVariable(
                name = "target_audience",
                description = "Intended audience",
                type = VariableType.STRING,
                required = false,
                examples = listOf("children", "young adults", "adults", "professionals")
            ),
            PromptVariable(
                name = "additional_requirements",
                description = "List of additional requirements",
                type = VariableType.LIST,
                required = false
            )
        ),
        category = "creative",
        tags = listOf("writing", "creative", "content")
    )

    val QUESTION_ANSWERING = PromptTemplateData(
        id = "question_answering",
        name = "Question Answering",
        description = "Structured question answering with context",
        template = """
            Please answer the following question based on the provided context. Be accurate, concise, and cite information from the context when possible.
            
            {% if context %}
            Context:
            {{ context }}
            
            {% endif %}
            Question: {{ question }}
            
            {% if answer_format %}
            Please format your answer as: {{ answer_format }}
            {% endif %}
            
            {% if include_sources %}
            Please include sources or references in your answer.
            {% endif %}
        """.trimIndent(),
        variables = listOf(
            PromptVariable(
                name = "question",
                description = "The question to answer",
                type = VariableType.STRING,
                required = true,
                examples = listOf("What is the capital of France?", "How does photosynthesis work?")
            ),
            PromptVariable(
                name = "context",
                description = "Relevant context information",
                type = VariableType.STRING,
                required = false
            ),
            PromptVariable(
                name = "answer_format",
                description = "Desired format for the answer",
                type = VariableType.STRING,
                required = false,
                examples = listOf("bullet points", "paragraph", "step by step")
            ),
            PromptVariable(
                name = "include_sources",
                description = "Whether to include sources",
                type = VariableType.BOOLEAN,
                required = false,
                defaultValue = false
            )
        ),
        category = "qa",
        tags = listOf("question", "answer", "information")
    )

    /**
     * Get all default prompt templates
     */
    fun getAllDefaultTemplates(): List<PromptTemplateData> {
        return listOf(
            CHAT_ASSISTANT,
            CODE_ASSISTANT,
            CREATIVE_WRITING,
            QUESTION_ANSWERING
        )
    }

    /**
     * Get default templates by category
     */
    fun getDefaultTemplatesByCategory(category: String): List<PromptTemplateData> {
        return getAllDefaultTemplates().filter { it.category == category }
    }
}