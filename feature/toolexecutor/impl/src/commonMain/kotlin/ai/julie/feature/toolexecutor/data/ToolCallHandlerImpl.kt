package ai.julie.feature.toolexecutor.data

import ai.julie.feature.toolexecutor.domain.ToolCallHandler

class ToolCallHandlerImpl : ToolCallHandler {
    override suspend fun handleModelOutput(modelOutput: String): String? {
        // TODO: Implement tool call parsing and execution
        return null
    }
    
    override suspend fun hasToolCalls(modelOutput: String): Boolean {
        // TODO: Implement tool call detection
        return false
    }
}