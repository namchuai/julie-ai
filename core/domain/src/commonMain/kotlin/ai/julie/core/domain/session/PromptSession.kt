package ai.julie.core.domain.session

import ai.julie.core.data.llama.Promptable
import ai.julie.core.model.LlamaSamplerSettings
import ai.julie.feature.jinjaparser.domain.ProcessChatTemplate
import ai.julie.feature.message.domain.model.EnrichedMessage
import ai.julie.feature.modelconfig.domain.preset.SamplingPreset
import ai.julie.logging.Logger
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.chat.Tool
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.coroutineContext

class PromptSession(
    val threadId: String,
    val modelId: String,
    private val prompt: Promptable,
    private val processChatTemplate: ProcessChatTemplate
) {
    private var currentJob: Job? = null
    private var state: PromptSessionState = PromptSessionState.Idle

    fun getState(): PromptSessionState = state

    fun isActive(): Boolean = state is PromptSessionState.Running

    fun prompt(
        messages: List<EnrichedMessage>,
        samplerSettings: SamplingPreset,
        template: String? = null,
        addGenerationPrompt: Boolean = false,
        bosToken: String? = null,
        dateString: String? = null,
        tools: List<Tool>? = null,
        builtinTools: List<String>? = null,
    ): Flow<String> = flow {
        Logger.d("PromptSession[$threadId]: Starting prompt with model $modelId")

        // Process input
        val processedInput = if (template != null) {
            processChatTemplate.processChatTemplate(
                template = template,
                messages = messages,
                addGenerationPrompt = addGenerationPrompt,
                bosToken = bosToken,
                dateString = dateString,
                tools = tools,
                builtinTools = builtinTools,
            )
        } else {
            (messages.lastOrNull()?.message?.content?.firstOrNull() as? MessageContent.Text)?.text?.value
                ?: ""
        }

        Logger.d("PromptSession[$threadId]: Processed input: $processedInput")
        val samplerSettings = LlamaSamplerSettings(
            temperature = samplerSettings.temperature.value,
            penaltyRepeat = samplerSettings.repeatPenalty.value,
            topK = samplerSettings.topK.value,
            topP = samplerSettings.topP.value,
            minP = samplerSettings.minP.value,
            mirostat = samplerSettings.mirostat.value.toInt(),
        )
        // Generate response
        prompt.prompt(processedInput, samplerSettings)
            .collect { token ->
                emit(token)
            }

        state = PromptSessionState.Finished
        Logger.d("PromptSession[$threadId]: Completed prompt")
    }.catch { e ->
        Logger.e("PromptSession[$threadId]: Error during prompt: ${e.message}")
        state = PromptSessionState.Error(e)
        throw e
    }.onStart {
        currentJob = coroutineContext[Job]
        state = PromptSessionState.Running(currentJob!!)
    }.onCompletion {
        if (state is PromptSessionState.Running) {
            state = PromptSessionState.Finished
        }
    }

    fun cancel() {
        Logger.d("PromptSession[$threadId]: Cancelling session")
        currentJob?.cancel()
        state = PromptSessionState.Cancelled
    }

    fun cleanup() {
        Logger.d("PromptSession[$threadId]: Cleaning up session")
        cancel()
    }
}