package ai.julie.feature.toolmanagement.domain

import com.aallam.openai.api.chat.Tool
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

data class EnrichedTool(
    val tool: Tool,
    val isEnabled: Boolean = true,
    val category: ToolCategory = ToolCategory.CUSTOM,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
) {
    // Delegate Tool properties for seamless access
    val function get() = tool.function
}

@Serializable
enum class ToolCategory {
    SEARCH,
    WEB_SCRAPING,
    API_CALL,
    FILE_OPERATION,
    COMPUTATION,
    CUSTOM
}