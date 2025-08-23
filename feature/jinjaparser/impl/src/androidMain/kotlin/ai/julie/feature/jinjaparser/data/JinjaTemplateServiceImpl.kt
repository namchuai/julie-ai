package ai.julie.feature.jinjaparser.data

import ai.julie.feature.jinjaparser.domain.ProcessChatTemplate
import ai.julie.feature.message.domain.model.EnrichedMessage
import ai.julie.feature.message.domain.model.EnrichedRole
import ai.julie.feature.message.domain.model.extractTextContent
import ai.julie.logging.Logger

actual class JinjaTemplateServiceImpl : ProcessChatTemplate {

    private val TAG = "JinjaTemplateService"

    // TODO: should passing the model metadata and user's own config here?
    actual override fun processChatTemplate(
        template: String,
        messages: List<EnrichedMessage>,
        addGenerationPrompt: Boolean,
        bosToken: String?,
        dateString: String?,
        tools: List<Tool>?,
        builtinTools: List<String>?,
    ): String {
        // Simple template processing for Android without jinjava
        // This is a basic implementation that handles simple variable substitution
        var processedTemplate = template
        
        val context = mutableMapOf<String, String>()
        context["current_message"] = messages.lastOrNull()?.extractTextContent() ?: ""
        
        val userMessages = messages.filter { it.role == EnrichedRole.User }
        val assistantMessages = messages.filter { it.role == EnrichedRole.Assistant }
        
        context["last_user_message"] = userMessages.lastOrNull()?.extractTextContent() ?: ""
        context["last_assistant_message"] = assistantMessages.lastOrNull()?.extractTextContent() ?: ""
        
        // Simple variable substitution
        context.forEach { (key, value) ->
            processedTemplate = processedTemplate.replace("{{ $key }}", value)
            processedTemplate = processedTemplate.replace("{{$key}}", value)
        }
        
        Logger.d("[$TAG] Processed template on Android")
        return processedTemplate
    }
}