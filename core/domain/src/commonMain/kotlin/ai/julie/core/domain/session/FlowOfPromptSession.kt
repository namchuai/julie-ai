package ai.julie.core.domain.session

import kotlinx.coroutines.flow.Flow

interface FlowOfPromptSession {
    fun flowOfPromptSession(threadId: String): Flow<PromptSessionState>
}