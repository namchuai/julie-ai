package ai.julie.feature.jinjaparser.data

import ai.julie.feature.jinjaparser.domain.ProcessChatTemplate
import ai.julie.feature.message.domain.model.EnrichedMessage
import com.aallam.openai.api.chat.Tool

expect class JinjaTemplateServiceImpl() : ProcessChatTemplate {
    override fun processChatTemplate(
        template: String,
        messages: List<EnrichedMessage>,
        addGenerationPrompt: Boolean,
        bosToken: String?,
        dateString: String?,
        tools: List<Tool>?,
        builtinTools: List<String>?,
    ): String
}