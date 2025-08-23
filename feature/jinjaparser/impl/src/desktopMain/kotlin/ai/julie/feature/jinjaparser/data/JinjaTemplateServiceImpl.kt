package ai.julie.feature.jinjaparser.data

import ai.julie.feature.jinjaparser.domain.ParseConversation
import ai.julie.feature.jinjaparser.domain.ProcessChatTemplate
import ai.julie.feature.message.domain.model.EnrichedMessage
import ai.julie.feature.message.domain.model.EnrichedRole
import ai.julie.feature.message.domain.model.extractTextContent
import ai.julie.logging.Logger
import com.aallam.openai.api.chat.Tool
import com.hubspot.jinjava.Jinjava

actual class JinjaTemplateServiceImpl : ProcessChatTemplate, ParseConversation {

    private val TAG = "JinjaTemplateService"

    actual override fun processChatTemplate(
        template: String,
        messages: List<EnrichedMessage>,
        addGenerationPrompt: Boolean,
        bosToken: String?,
        dateString: String?,
        tools: List<Tool>?,
        builtinTools: List<String>?,
    ): String {
        val context = mutableMapOf<String, Any>()

        // Add conversation context
        context["messages"] = messages.map { message ->
            mapOf(
                "role" to message.role.value,
                "content" to message.extractTextContent(),
            )
        }

        context["current_message"] = messages.lastOrNull()?.extractTextContent() ?: ""

        // Add required Jinja template variables
        context["add_generation_prompt"] = addGenerationPrompt
        if (bosToken != null) {
            context["bos_token"] = bosToken
        }
        if (dateString != null) {
            context["date_string"] = dateString
        }
        
        // Add tools if provided
        if (tools != null) {
            context["tools"] = tools
        }
        
        // Add builtin_tools if provided (for Llama 3.1 built-in tools like brave_search)
        if (builtinTools != null) {
            context["builtin_tools"] = builtinTools
        }

        // Add conversation history as separate variables
        val userMessages = messages.filter { it.role == EnrichedRole.User }.map { it.content }
        val assistantMessages =
            messages.filter { it.role == EnrichedRole.Assistant }.map { it.content }

        context["user_messages"] = userMessages
        context["assistant_messages"] = assistantMessages
        context["last_user_message"] = userMessages.lastOrNull() ?: ""
        context["last_assistant_message"] = assistantMessages.lastOrNull() ?: ""

        return try {
            val jinja = Jinjava()
            jinja.render(template, context)
        } catch (e: Exception) {
            Logger.e("[$TAG] Error processing template: ${e.message}")
            // Return original template if processing fails
            template
        }
    }

    override fun parseConversation(
        messages: List<EnrichedMessage>,
        context: Map<String, Any>
    ) {
        TODO("Not yet implemented")
    }
}