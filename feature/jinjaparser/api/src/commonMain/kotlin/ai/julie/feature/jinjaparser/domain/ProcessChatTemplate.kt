package ai.julie.feature.jinjaparser.domain

import ai.julie.feature.message.domain.model.EnrichedMessage
import ai.julie.core.model.LlamaSamplerSettings
import com.aallam.openai.api.chat.Tool

interface ProcessChatTemplate {
    fun processChatTemplate(
        template: String,
        messages: List<EnrichedMessage>,
        addGenerationPrompt: Boolean,
        bosToken: String?,
        dateString: String?,
        tools: List<Tool>? = null,
    ): String
}