package ai.julie.feature.chat.ui.chatinput

import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import ai.julie.core.domain.session.FlowOfPromptSession
import ai.julie.core.domain.session.PromptSessionState
import ai.julie.core.eventbus.ErrorEvent
import ai.julie.core.eventbus.ErrorType
import ai.julie.core.eventbus.EventBus
import ai.julie.feature.chat.domain.LocalInferenceUseCase
import ai.julie.feature.chat.domain.SimpleInferenceUseCase
import ai.julie.feature.message.domain.CreateMessage
import ai.julie.feature.message.domain.model.EnrichedRole
import ai.julie.feature.modelconfig.domain.FlowOfSamplingPresets
import ai.julie.feature.modelconfig.domain.preset.SamplingPreset
import ai.julie.feature.modelmanagement.domain.FlowOfRunningModels
import ai.julie.feature.modelmanagement.domain.FlowOfStartingModels
import ai.julie.feature.modelmanagement.domain.ValidateModel
import ai.julie.feature.thread.domain.FlowOfActiveThread
import ai.julie.feature.thread.domain.model.EnrichedThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatInputViewModel(
    private val flowOfActiveThread: FlowOfActiveThread,
    private val flowOfStartingModels: FlowOfStartingModels,
    private val flowOfRunningModels: FlowOfRunningModels,
    private val flowOfSamplingPresets: FlowOfSamplingPresets,
    private val flowOfPromptSession: FlowOfPromptSession,
    private val createMessage: CreateMessage,
    private val localInferenceUseCase: LocalInferenceUseCase,
    private val simpleInferenceUseCase: SimpleInferenceUseCase,
    private val validateModel: ValidateModel,
    private val eventBus: EventBus,
) : ViewModel() {

    // Flag to switch between implementations - set to true to use new orchestrator approach
    private val useNewInferenceApproach = true

    val state = viewModelState(
        initialState = ChatInputState(message = ""),
        savedStateBehaviour = doNotSaveState(),
        loadTimeReporter = doNotReportLoadTime()
    )

    private var activeThread: EnrichedThread? = null

    fun onMessageUpdate(message: String) {
        state.value = state.value.copy(message = message)
    }

    fun onSendClick() {
        if (state.value.message.isBlank()) return

        activeThread?.let { thread ->
            viewModelScope.launch {
                // Check if there's already an active session
                if (state.value.sessionState is PromptSessionState.Running) {
                    // TODO: show message that session is already running
                    return@launch
                }

//            val modelIsStarting = flowOfStartingModels.flowOfStartingModels().firstOrNull()?.let {
//                it.any { startingModels ->
//                    startingModels.key.id == thread.modelId
//                }
//            } ?: false
//            val isModelAlreadyRunning =
//                flowOfRunningModels.flowOfRunningModels().firstOrNull()?.let {
//                    it.any { model -> model.id == thread.modelId }
//                } ?: false

//            if (!modelIsStarting && !isModelAlreadyRunning) {
//                // TODO: display popup, starting the model
//                return@launch
//            }
//
//            if (modelIsStarting) {
//                // TODO: queue the message to be sent
//                return@launch
//            }

                // First, validate the model file exists
                try {
                    validateModel(thread.modelId)
                } catch (e: IllegalArgumentException) {
                    println("Model validation failed: ${e.message}")
                    eventBus.emit(
                        ErrorEvent(
                            title = "Model Not Found",
                            message = e.message ?: "The model file could not be found",
                            type = ErrorType.ModelNotFound
                        )
                    )
                    return@launch
                } catch (e: Exception) {
                    println("Model validation error: ${e.message}")
                    eventBus.emit(
                        ErrorEvent(
                            title = "Error",
                            message = "Failed to validate model: ${e.message}",
                            type = ErrorType.General
                        )
                    )
                    return@launch
                }

                // Only create message and proceed if model is valid
                createMessage.createMessage(
                    threadId = thread.id,
                    content = state.value.message,
                    role = EnrichedRole.User,
                )

                state.update {
                    it.copy(message = "")
                }

                if (useNewInferenceApproach) {
                    // Use the new orchestrator-based approach
                    simpleInferenceUseCase.invoke(
                        threadId = thread.id,
                        modelId = thread.modelId,
                        samplingPreset = selectedSamplingPreset!!,
                    )
                } else {
                    // Use the old approach
                    localInferenceUseCase.invoke(
                        threadId = thread.id,
                        modelId = thread.modelId,
                        samplingPreset = selectedSamplingPreset!!,
                    )
                }
            }
        } ?: run {
            println("No active thread found")
        }
    }

    fun onCancelClick() {
        viewModelScope.launch {
            val thread = flowOfActiveThread.flowOfActiveThread().firstOrNull()
            if (thread != null) {
                localInferenceUseCase.cancelInference(thread.id)
            }
        }
    }

    private var selectedSamplingPreset: SamplingPreset? = null

    init {
        combine(
            flowOfActiveThread.flowOfActiveThread().filterNotNull(),
            flowOfSamplingPresets.flowOfSamplingPresets()
        ) { thread, presets ->
            activeThread = thread
            selectedSamplingPreset =
                presets.firstOrNull { it.id == thread.samplingPresetId } ?: presets.firstOrNull()
        }.launchIn(viewModelScope)

        flowOfActiveThread.flowOfActiveThread()
            .filterNotNull()
            .flatMapLatest { thread ->
                flowOfPromptSession.flowOfPromptSession(thread.id)
            }.onEach { sessionState ->
                state.update { it.copy(sessionState = sessionState) }
            }
            .launchIn(viewModelScope)
    }
}
