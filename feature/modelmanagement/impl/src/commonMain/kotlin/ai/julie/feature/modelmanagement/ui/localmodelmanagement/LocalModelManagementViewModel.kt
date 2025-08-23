package ai.julie.feature.modelmanagement.ui.localmodelmanagement

import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import ai.julie.core.model.aimodel.LocalModel
import ai.julie.feature.modelmanagement.domain.AddLocalModelFromFileUseCase
import ai.julie.feature.modelmanagement.domain.DeleteLocalModelUseCase
import ai.julie.feature.modelmanagement.domain.FlowOfLocalModels
import ai.julie.feature.thread.domain.CreateThread
import ai.julie.logging.Logger
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocalModelManagementViewModel(
    flowOfLocalModels: FlowOfLocalModels,
    private val addLocalModelFromFileUseCase: AddLocalModelFromFileUseCase,
    private val deleteLocalModelUseCase: DeleteLocalModelUseCase,
    private val createThread: CreateThread,
) : ViewModel() {

    val state = viewModelState(
        savedStateBehaviour = doNotSaveState(),
        initialState = LocalModelManagementState(),
        loadTimeReporter = doNotReportLoadTime(),
    )

    fun onAddLocalModelClick() {
        viewModelScope.launch {
            try {
                val file = FileKit.openFilePicker()

                if (file == null) {
                    return@launch
                }
                addLocalModelFromFileUseCase(file)
            } catch (e: Exception) {
                Logger.e("Failed to add local model: $e")
            }
        }
    }

    fun onDeleteModel(modelId: String) {
        viewModelScope.launch {
            deleteLocalModelUseCase.invoke(modelId)
        }
    }

    fun onRunModelAndNavigateToChat(
        model: LocalModel,
        onNavigateToChat: () -> Unit,
    ) {
        // TODO: display loading

        // Then do the async work
        viewModelScope.launch {
            val thread = createThread.createThread(
                modelId = model.id,
                title = "Chat with ${model.title}"
            )
            Logger.i("Thread created with id: ${thread.id}")
        }
    }

    init {
        flowOfLocalModels.flowOfLocalModels().onEach { models ->
            state.update { currentState ->
                currentState.copy(
                    models = models,
                )
            }
        }.launchIn(viewModelScope)
    }
}
