package ai.julie.feature.jinjaparser.domain

import ai.julie.feature.message.domain.model.EnrichedMessage
import com.aallam.openai.api.chat.Tool

interface ProcessChatTemplate {
    fun processChatTemplate(
        template: String,
        messages: List<EnrichedMessage>,
        addGenerationPrompt: Boolean,
        bosToken: String?,
        dateString: String?,
        tools: List<Tool>? = null,
        builtinTools: List<String>? = null,
    ): String
}