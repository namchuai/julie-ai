package ai.julie.feature.jinjaparser.data

import ai.julie.feature.message.domain.model.EnrichedMessage
import ai.julie.feature.message.domain.model.EnrichedRole
import ai.julie.feature.message.domain.model.extractTextContent
import ai.julie.core.model.LlamaSamplerSettings
import ai.julie.feature.jinjaparser.domain.ProcessChatTemplate
import ai.julie.logging.Logger
import com.aallam.openai.api.chat.Tool

actual class JinjaTemplateServiceImpl : ProcessChatTemplate {

    private val TAG = "JinjaTemplateService"

    actual override fun processChatTemplate(
        template: String,
        messages: List<EnrichedMessage>,
        addGenerationPrompt: Boolean,
        bosToken: String?,
        dateString: String?,
        tools: List<Tool>?,
    ): String {
        // Simple template processing for iOS without jinjava
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
        
        Logger.d("[$TAG] Processed template on iOS")
        return processedTemplate
    }
}