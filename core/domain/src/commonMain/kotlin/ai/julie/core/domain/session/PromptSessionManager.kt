package ai.julie.core.domain.session

import ai.julie.core.data.llama.Promptable
import ai.julie.core.model.LlamaSamplerSettings
import ai.julie.feature.jinjaparser.domain.ProcessChatTemplate
import ai.julie.feature.message.domain.model.EnrichedMessage
import ai.julie.feature.modelconfig.domain.preset.SamplingPreset
import ai.julie.logging.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import com.aallam.openai.api.chat.Tool

interface PromptSessionManager {

    suspend fun startNewSession(
        threadId: String,
        modelId: String,
        promptable: Promptable,
    ): PromptSession

    suspend fun getSession(threadId: String): PromptSession?
    suspend fun getSessionState(threadId: String): PromptSessionState
    suspend fun isSessionActive(threadId: String): Boolean

    suspend fun prompt(
        threadId: String,
        messages: List<EnrichedMessage>,
        samplerSettings: SamplingPreset,
        template: String? = null,
        addGenerationPrompt: Boolean = false,
        bosToken: String? = null,
        dateString: String? = null,
        tools: List<Tool>? = null,
    ): Flow<String>

    suspend fun cancelSession(threadId: String): Boolean
    suspend fun cleanupSession(threadId: String)
    suspend fun cleanupAllSessions()
}

class PromptSessionManagerImpl(
    private val processChatTemplate: ProcessChatTemplate
) : PromptSessionManager, FlowOfPromptSession {

    private val activeSessions = mutableMapOf<String, PromptSession>()
    private val sessionStates = mutableMapOf<String, MutableStateFlow<PromptSessionState>>()

    override suspend fun startNewSession(
        threadId: String,
        modelId: String,
        promptable: Promptable,
    ): PromptSession {
        Logger.d("PromptSessionManager: Starting session for thread $threadId with existing repository for model $modelId")

        // Always clean up any existing session before creating new one
        val existingSession = activeSessions[threadId]
        existingSession?.cleanup()

        val session = PromptSession(
            threadId = threadId,
            modelId = modelId,
            prompt = promptable,
            processChatTemplate = processChatTemplate
        )

        activeSessions[threadId] = session

        // Initialize state flow for this thread if not exists
        if (!sessionStates.containsKey(threadId)) {
            sessionStates[threadId] = MutableStateFlow(PromptSessionState.Idle)
        }

        Logger.d("PromptSessionManager: Session created for thread $threadId using existing repository")

        return session
    }

    override suspend fun getSession(threadId: String): PromptSession? {
        return activeSessions[threadId]
    }

    override suspend fun getSessionState(threadId: String): PromptSessionState {
        return activeSessions[threadId]?.getState() ?: PromptSessionState.Idle
    }

    override suspend fun isSessionActive(threadId: String): Boolean {
        return activeSessions[threadId]?.isActive() ?: false
    }

    override suspend fun prompt(
        threadId: String,
        messages: List<EnrichedMessage>,
        samplerSettings: SamplingPreset,
        template: String?,
        addGenerationPrompt: Boolean,
        bosToken: String?,
        dateString: String?,
        tools: List<Tool>?,
    ): Flow<String> {
        val session = activeSessions[threadId]
        require(session != null) { "No active session found for thread $threadId. Start a session first." }

        // Update state flow
        sessionStates[threadId]?.value = PromptSessionState.Running(kotlinx.coroutines.Job())

        return session.prompt(
            messages = messages,
            samplerSettings = samplerSettings,
            template = template,
            addGenerationPrompt = addGenerationPrompt,
            bosToken = bosToken,
            dateString = dateString,
            tools = tools,
        ).onCompletion {
            // Update state when completed
            sessionStates[threadId]?.value = when (session.getState()) {
                is PromptSessionState.Error -> session.getState()
                is PromptSessionState.Cancelled -> PromptSessionState.Cancelled
                else -> PromptSessionState.Finished
            }
        }
    }

    override suspend fun cancelSession(threadId: String): Boolean {
        val session = activeSessions[threadId]
        return if (session != null) {
            session.cancel()
            sessionStates[threadId]?.value = PromptSessionState.Cancelled
            Logger.d("PromptSessionManager: Cancelled session for thread $threadId")
            true
        } else {
            false
        }
    }

    override suspend fun cleanupSession(threadId: String) {
        activeSessions.remove(threadId)?.let { session ->
            Logger.d("PromptSessionManager: Cleaning up session for thread $threadId")
            session.cleanup()
        }
        sessionStates[threadId]?.value = PromptSessionState.Idle
    }

    override suspend fun cleanupAllSessions() {
        Logger.d("PromptSessionManager: Cleaning up all sessions")
        activeSessions.values.forEach { session ->
            session.cleanup()
        }
        activeSessions.clear()
        sessionStates.forEach { (threadId, _) ->
            sessionStates[threadId]?.value = PromptSessionState.Idle
        }
    }

    override fun flowOfPromptSession(threadId: String): Flow<PromptSessionState> {
        // Create state flow if it doesn't exist
        if (!sessionStates.containsKey(threadId)) {
            sessionStates[threadId] = MutableStateFlow(PromptSessionState.Idle)
        }
        return sessionStates[threadId]!!.asStateFlow()
    }
}