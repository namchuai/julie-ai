package ai.julie.feature.modelmanagement.ui.modelconfig

import ai.julie.core.common.createAsyncLoading
import ai.julie.core.common.createAsyncSuccess
import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import ai.julie.core.model.ModelContextParams
import ai.julie.core.model.ModelLoadParams
import ai.julie.core.model.aimodel.LocalModel
import ai.julie.feature.modelconfig.domain.FlowOfModelContextParam
import ai.julie.feature.modelconfig.domain.FlowOfModelLoadParam
import ai.julie.feature.modelconfig.domain.FlowOfModelMetadata
import ai.julie.feature.modelconfig.domain.FlowOfSamplingPresets
import ai.julie.feature.modelconfig.domain.RecreateModelContextUseCase
import ai.julie.feature.modelconfig.domain.StoreModelContextParam
import ai.julie.feature.modelconfig.domain.StoreModelLoadParam
import ai.julie.feature.modelconfig.domain.gguf.LlamaModelMetadata
import ai.julie.feature.modelconfig.domain.gguf.deepseek2.Deepseek2ModelMetadata
import ai.julie.feature.modelconfig.domain.gguf.qwen3.Qwen3MoeModelMetadata
import ai.julie.feature.modelconfig.domain.preset.SamplingPreset
import ai.julie.feature.thread.domain.FlowOfActiveThread
import ai.julie.feature.thread.domain.UpdateThreadSamplingPresetId
import ai.julie.feature.modelmanagement.domain.FlowOfLocalModels
import ai.julie.feature.modelmanagement.domain.FlowOfRunningModels
import ai.julie.feature.modelmanagement.domain.ReloadModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ModelConfigViewModel(
    private val flowOfActiveThread: FlowOfActiveThread,
    private val flowOfSamplingPresets: FlowOfSamplingPresets,
    private val flowOfModelMetadata: FlowOfModelMetadata,
    private val flowOfModelContextParam: FlowOfModelContextParam,
    private val flowOfModelLoadParam: FlowOfModelLoadParam,
    private val storeSelectedSamplingPreset: UpdateThreadSamplingPresetId,
    private val storeModelContextParam: StoreModelContextParam,
    private val storeModelLoadParam: StoreModelLoadParam,
    private val recreateModelContextUseCase: RecreateModelContextUseCase,
    private val flowOfRunningModels: FlowOfRunningModels,
    private val flowOfLocalModels: FlowOfLocalModels,
    private val reloadModel: ReloadModel,
) : ViewModel() {

    private lateinit var modelId: String
    private var currentModel: LocalModel? = null

    // Cache the latest persisted model context params to preserve unrelated fields on updates
    private var currentContextParams: ModelContextParams? = null
    private var currentLoadParams: ModelLoadParams? = null
    
    // Debouncing for context recreation
    private var contextRecreationJob: Job? = null
    private val CONTEXT_RECREATION_DELAY_MS = 3000L

    val state = viewModelState(
        savedStateBehaviour = doNotSaveState(),
        loadTimeReporter = doNotReportLoadTime(),
        initialState = ModelConfigState(
            samplingPresetState = createAsyncLoading(),
            modelContextState = createAsyncLoading(),
            modelLoadState = createAsyncLoading(),
        ),
    )

    fun onPresetSelected(samplingPreset: SamplingPreset) {
        viewModelScope.launch {
            flowOfActiveThread.flowOfActiveThread().first()?.let {
                storeSelectedSamplingPreset.updateThreadSamplingPresetId(
                    threadId = it.id,
                    samplingPresetId = samplingPreset.id,
                )
            }
        }
    }

    private fun scheduleContextRecreation(params: ModelContextParams, operation: String) {
        // Cancel any pending recreation
        contextRecreationJob?.cancel()
        
        // Schedule new recreation with 3-second delay
        contextRecreationJob = viewModelScope.launch {
            delay(CONTEXT_RECREATION_DELAY_MS)
            try {
                recreateModelContextUseCase.recreateModelContext(params)
                println("Context recreated successfully for $operation after ${CONTEXT_RECREATION_DELAY_MS}ms delay")
            } catch (e: Exception) {
                println("Failed to recreate context for $operation: ${e.message}")
                // TODO: Handle error (show user notification, revert UI state, etc.)
            }
        }
    }

    fun onContextLengthUpdate(contextLength: Float) {
        viewModelScope.launch {
            val current = currentContextParams ?: return@launch
            if (!::modelId.isInitialized) return@launch

            val currentMc =
                (state.value.modelContextState as? ai.julie.core.common.AsyncState.Success<ModelContextData>)?.value
                    ?: return@launch

            val maxCtx = currentMc.contextLength.max.toInt()
            val newNCtx = contextLength.roundToInt().coerceIn(1, maxCtx)

            val adjustedNBatch = current.nBatch.coerceAtMost(newNCtx)
            val adjustedNUbatch = current.nUbatch.coerceAtMost(adjustedNBatch)

            val params =
                current.copy(nCtx = newNCtx, nBatch = adjustedNBatch, nUbatch = adjustedNUbatch)
            currentContextParams = params

            // Optimistically update UI state
            state.update {
                it.copy(
                    modelContextState = createAsyncSuccess(
                        currentMc.copy(
                            contextLength = currentMc.contextLength.copy(value = newNCtx.toULong()),
                            nBatch = currentMc.nBatch.copy(
                                max = newNCtx.toULong(),
                                value = adjustedNBatch.toULong()
                            ),
                            nUBatch = currentMc.nUBatch.copy(
                                max = adjustedNBatch.toULong(),
                                value = adjustedNUbatch.toULong()
                            ),
                        )
                    )
                )
            }

            // Persist
            storeModelContextParam.storeModelContextParam(
                modelId = modelId,
                modelContextParams = params
            )

            // Schedule debounced context recreation
            scheduleContextRecreation(params, "context length update")
        }
    }

    fun onNBatchUpdate(nBatch: Float) {
        viewModelScope.launch {
            val current = currentContextParams ?: return@launch
            if (!::modelId.isInitialized) return@launch

            val currentMc =
                (state.value.modelContextState as? ai.julie.core.common.AsyncState.Success<ModelContextData>)?.value
                    ?: return@launch

            val newNBatch = nBatch.roundToInt().coerceIn(1, current.nCtx)
            val adjustedNUbatch = current.nUbatch.coerceAtMost(newNBatch)

            val params = current.copy(nBatch = newNBatch, nUbatch = adjustedNUbatch)
            currentContextParams = params

            // Optimistically update UI state
            state.update {
                it.copy(
                    modelContextState = createAsyncSuccess(
                        currentMc.copy(
                            nBatch = currentMc.nBatch.copy(value = newNBatch.toULong()),
                            nUBatch = currentMc.nUBatch.copy(
                                max = newNBatch.toULong(),
                                value = adjustedNUbatch.toULong()
                            ),
                        )
                    )
                )
            }

            // Persist
            storeModelContextParam.storeModelContextParam(
                modelId = modelId,
                modelContextParams = params
            )

            // Schedule debounced context recreation
            scheduleContextRecreation(params, "nBatch update")
        }
    }

    fun onNUBatchUpdate(nUBatch: Float) {
        viewModelScope.launch {
            val current = currentContextParams ?: return@launch
            if (!::modelId.isInitialized) return@launch

            val currentMc =
                (state.value.modelContextState as? ai.julie.core.common.AsyncState.Success<ModelContextData>)?.value
                    ?: return@launch

            val newNUbatch = nUBatch.roundToInt().coerceIn(1, current.nBatch)
            val params = current.copy(nUbatch = newNUbatch)
            currentContextParams = params

            // Optimistically update UI state
            state.update {
                it.copy(
                    modelContextState = createAsyncSuccess(
                        currentMc.copy(
                            nUBatch = currentMc.nUBatch.copy(value = newNUbatch.toULong()),
                        )
                    )
                )
            }

            // Persist
            storeModelContextParam.storeModelContextParam(
                modelId = modelId,
                modelContextParams = params
            )

            // Schedule debounced context recreation
            scheduleContextRecreation(params, "nUBatch update")
        }
    }

    fun onGpuLayersUpdate(gpuLayers: Int) {
        viewModelScope.launch {
            val current = currentLoadParams ?: return@launch
            if (!::modelId.isInitialized) return@launch

            val params = current.copy(nGpuLayers = gpuLayers)
            currentLoadParams = params

            // Update UI state to show pending reload
            val currentState = (state.value.modelLoadState as? ai.julie.core.common.AsyncState.Success<ModelLoadData>)?.value
                ?: return@launch

            state.update {
                it.copy(
                    modelLoadState = createAsyncSuccess(
                        currentState.copy(
                            currentParams = params,
                            pendingReload = true
                        )
                    )
                )
            }

            // Persist the new parameters
            storeModelLoadParam.storeModelLoadParam(
                modelId = modelId,
                modelLoadParams = params
            )
        }
    }

    fun onUseMlockUpdate(useMlock: Boolean) {
        viewModelScope.launch {
            val current = currentLoadParams ?: return@launch
            if (!::modelId.isInitialized) return@launch

            val params = current.copy(useMlock = useMlock)
            currentLoadParams = params

            // Update UI state to show pending reload
            val currentState = (state.value.modelLoadState as? ai.julie.core.common.AsyncState.Success<ModelLoadData>)?.value
                ?: return@launch

            state.update {
                it.copy(
                    modelLoadState = createAsyncSuccess(
                        currentState.copy(
                            currentParams = params,
                            pendingReload = true
                        )
                    )
                )
            }

            // Persist the new parameters
            storeModelLoadParam.storeModelLoadParam(
                modelId = modelId,
                modelLoadParams = params
            )
        }
    }

    fun onUseMmapUpdate(useMmap: Boolean) {
        viewModelScope.launch {
            val current = currentLoadParams ?: return@launch
            if (!::modelId.isInitialized) return@launch

            val params = current.copy(useMmap = useMmap)
            currentLoadParams = params

            // Update UI state to show pending reload
            val currentState = (state.value.modelLoadState as? ai.julie.core.common.AsyncState.Success<ModelLoadData>)?.value
                ?: return@launch

            state.update {
                it.copy(
                    modelLoadState = createAsyncSuccess(
                        currentState.copy(
                            currentParams = params,
                            pendingReload = true
                        )
                    )
                )
            }

            // Persist the new parameters
            storeModelLoadParam.storeModelLoadParam(
                modelId = modelId,
                modelLoadParams = params
            )
        }
    }

    fun onReloadModel() {
        viewModelScope.launch {
            if (!::modelId.isInitialized) return@launch
            val threadId = flowOfActiveThread.flowOfActiveThread().first()?.id
            if (threadId == null) {
                println("No active thread found for model reload")
                return@launch
            }
            val model = currentModel ?: return@launch

            // Clear the pending reload flag
            val currentState = (state.value.modelLoadState as? ai.julie.core.common.AsyncState.Success<ModelLoadData>)?.value
                ?: return@launch

            state.update {
                it.copy(
                    modelLoadState = createAsyncSuccess(
                        currentState.copy(pendingReload = false)
                    )
                )
            }

            try {
                // Trigger model reload through ReloadModel interface
                reloadModel.reloadModel(threadId, model)
                println("Model ${model.title} reloaded successfully for thread $threadId")
            } catch (e: Exception) {
                println("Failed to reload model ${model.title}: ${e.message}")
                // Restore pending reload flag on error
                state.update {
                    it.copy(
                        modelLoadState = createAsyncSuccess(
                            currentState.copy(pendingReload = true)
                        )
                    )
                }
            }
        }
    }

    init {
        flowOfActiveThread.flowOfActiveThread().filterNotNull().flatMapLatest {
            println("ModelConfigViewModel: Loading metadata for modelId: ${it.modelId}")
            combine(
                flowOfModelMetadata.flowOfModelMetadata(it.modelId),
                flowOfModelContextParam.flowOfModelContextParam(it.modelId),
                flowOfModelLoadParam.flowOfModelLoadParam(it.modelId)
            ) { metadata, contextParam, loadParam ->
                Triple(metadata, contextParam, loadParam)
            }
        }.onEach { (metadata, contextParam, loadParam) ->
            when (metadata) {
                is LlamaModelMetadata -> {
                    println("LlamaModelMetadata detected: ${metadata.contextLength}")
                    println("ContextParam: $contextParam")

                    // Keep cache up to date with the latest persisted params
                    currentContextParams = contextParam
                    currentLoadParams = loadParam

                    state.update {
                        it.copy(
                            modelContextState = createAsyncSuccess(
                                ModelContextData(
                                    contextLength = ContextLengthParam(
                                        min = 1,
                                        max = metadata.contextLength.value,
                                        step = metadata.contextLength.value.toInt(),
                                        value = contextParam.nCtx.toULong(),
                                    ),
                                    nBatch = ContextLengthParam(
                                        min = 1,
                                        max = contextParam.nCtx.toULong(),
                                        step = contextParam.nCtx,
                                        value = contextParam.nBatch.toULong(),
                                    ),
                                    nUBatch = ContextLengthParam(
                                        min = 1,
                                        max = contextParam.nBatch.toULong(),
                                        step = contextParam.nBatch,
                                        value = contextParam.nUbatch.toULong(),
                                    ),
                                )
                            ),
                            modelLoadState = createAsyncSuccess(
                                ModelLoadData(
                                    currentParams = loadParam,
                                    pendingReload = false
                                )
                            )
                        )
                    }
                }

                is Deepseek2ModelMetadata -> {
                    println("Deepseek2ModelMetadata detected: ${metadata.contextLength}")
                    println("  metadata.contextLength.value = ${metadata.contextLength.value}")
                    println("ContextParam: $contextParam")

                    // Keep cache up to date with the latest persisted params
                    currentContextParams = contextParam
                    currentLoadParams = loadParam

                    val stepSize = maxOf(1024, metadata.contextLength.value.toInt() / 32)

                    state.update {
                        it.copy(
                            modelContextState = createAsyncSuccess(
                                ModelContextData(
                                    contextLength = ContextLengthParam(
                                        min = 1,
                                        max = metadata.contextLength.value,
                                        step = stepSize,
                                        value = contextParam.nCtx.toULong(),
                                    ),
                                    nBatch = ContextLengthParam(
                                        min = 1,
                                        max = contextParam.nCtx.toULong(),
                                        step = contextParam.nCtx,
                                        value = contextParam.nBatch.toULong(),
                                    ),
                                    nUBatch = ContextLengthParam(
                                        min = 1,
                                        max = contextParam.nBatch.toULong(),
                                        step = contextParam.nBatch,
                                        value = contextParam.nUbatch.toULong(),
                                    ),
                                )
                            ),
                            modelLoadState = createAsyncSuccess(
                                ModelLoadData(
                                    currentParams = loadParam,
                                    pendingReload = false
                                )
                            )
                        )
                    }
                }

                is Qwen3MoeModelMetadata -> {
                    // Keep cache up to date with the latest persisted params
                    currentContextParams = contextParam
                    currentLoadParams = loadParam

                    state.update {
                        it.copy(
                            modelContextState = createAsyncSuccess(
                                ModelContextData(
                                    contextLength = ContextLengthParam(
                                        min = 1,
                                        max = metadata.contextLength.value,
                                        step = metadata.contextLength.value.toInt(),
                                        value = contextParam.nCtx.toULong(),
                                    ),
                                    nBatch = ContextLengthParam(
                                        min = 1,
                                        max = contextParam.nCtx.toULong(),
                                        step = contextParam.nCtx,
                                        value = contextParam.nBatch.toULong(),
                                    ),
                                    nUBatch = ContextLengthParam(
                                        min = 1,
                                        max = contextParam.nBatch.toULong(),
                                        step = contextParam.nBatch,
                                        value = contextParam.nUbatch.toULong(),
                                    ),
                                )
                            ),
                            modelLoadState = createAsyncSuccess(
                                ModelLoadData(
                                    currentParams = loadParam,
                                    pendingReload = false
                                )
                            )
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)

        combine(
            flowOfActiveThread.flowOfActiveThread(),
            flowOfSamplingPresets.flowOfSamplingPresets(),
            flowOfLocalModels.flowOfLocalModels(),
        ) { thread, presets, localModels ->
            thread?.let {
                modelId = thread.modelId
                currentModel = localModels.find { model -> model.id == thread.modelId }
                
                state.update {
                    it.copy(
                        samplingPresetState = createAsyncSuccess(
                            SamplingPresetData(
                                samplingPresetList = presets,
                                selectedSamplingPreset = presets.firstOrNull { it.id == thread.samplingPresetId }
                                    ?: presets.firstOrNull()!!
                            ),
                        )
                    )

                }
            }
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel any pending context recreation
        contextRecreationJob?.cancel()
    }
}
