package ai.julie.core.domain.session

import kotlinx.coroutines.Job

sealed class PromptSessionState {
    object Idle : PromptSessionState()
    data class Running(val job: Job) : PromptSessionState()
    object Finished : PromptSessionState()
    object Cancelled : PromptSessionState()
    data class Error(val exception: Throwable) : PromptSessionState()
}