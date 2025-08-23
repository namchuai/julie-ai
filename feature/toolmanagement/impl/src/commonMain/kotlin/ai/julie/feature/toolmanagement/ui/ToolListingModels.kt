package ai.julie.feature.toolmanagement.ui

import ai.julie.feature.toolmanagement.domain.EnrichedTool

data class ToolListingState(
    val tools: List<EnrichedTool>
)