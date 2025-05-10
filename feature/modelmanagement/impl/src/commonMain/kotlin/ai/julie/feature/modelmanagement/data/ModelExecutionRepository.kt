package ai.julie.feature.modelmanagement.data

import ai.julie.core.data.llama.LlamaRepository
import ai.julie.core.model.ModelContextParams
import ai.julie.core.model.ModelLoadParams
import ai.julie.core.model.aimodel.LocalModel
import ai.julie.feature.modelconfig.domain.FlowOfModelContextParam
import ai.julie.feature.modelconfig.domain.FlowOfModelLoadParam
import ai.julie.feature.modelmanagement.domain.FlowOfRunningModels
import ai.julie.feature.modelmanagement.domain.FlowOfStartingModels
import ai.julie.feature.modelmanagement.domain.ReloadModel
import ai.julie.feature.modelmanagement.domain.StartModel
import ai.julie.feature.modelmanagement.domain.StopAllModels
import ai.julie.feature.modelmanagement.domain.StopModel
import ai.julie.llamabinding.LlamaProgressCallback
import ai.julie.logging.Logger
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.exists
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ModelExecutionKey(
    val threadId: String,
    val modelId: String
)

data class ModelExecution(
    val job: Job,
    val model: LocalModel,
    val repository: LlamaRepository
)

class ModelExecutionRepository(
    private val llamaRepositoryFactory: () -> LlamaRepository,
    private val modelLoadParamRepository: FlowOfModelLoadParam,
    private val modelContextParamRepository: FlowOfModelContextParam
) : FlowOfRunningModels, FlowOfStartingModels, StartModel, StopModel, StopAllModels, ReloadModel {

    private val TAG = "ModelExecutionRepository"

    private val modelExecutionExceptionHandler = CoroutineExceptionHandler { context, exception ->
        Logger.e("[$TAG] Model execution failed: ${exception.message}")
        // Find and remove the failed job from running models
        val failedEntry = runningModelExecutions.entries.find { it.value.job == context[Job] }
        failedEntry?.let { (key, execution) ->
            Logger.e("[$TAG] Removing failed model: ${key.modelId} for thread ${key.threadId}")
            // Clean up the repository in a coroutine since cleanUp is suspend
            modelExecutionScope.launch {
                try {
                    execution.repository.cleanUp()
                } catch (e: Exception) {
                    Logger.e("[$TAG] Error cleaning up failed model repository: ${e.message}")
                }
            }
            runningModelExecutions.remove(key)
            updateRunningModelsState()
        }
    }

    private val modelExecutionScope: CoroutineScope = CoroutineScope(
        context = SupervisorJob() + Dispatchers.Default + modelExecutionExceptionHandler
    )

    // Track running model executions by (threadId, modelId) composite key
    private val runningModelExecutions = mutableMapOf<ModelExecutionKey, ModelExecution>()

    private val runningModels = MutableStateFlow<Set<LocalModel>>(value = emptySet())
    private val startingModels = MutableStateFlow<Map<LocalModel, Float>>(value = emptyMap())

    override suspend fun startModel(threadId: String, model: LocalModel) {
        // Validate model file exists
        val filePath = model.localPath
        require(!filePath.isNullOrEmpty()) {
            "Cannot prepare properties for different ViewModels at the same time."
        }

        val file = PlatformFile(filePath)
        require(file.exists()) {
            "Model file not found: $filePath"
        }

        val executionKey = ModelExecutionKey(threadId, model.id)

        // Check if this exact model+thread combination is already running
        if (runningModelExecutions.containsKey(executionKey)) {
            Logger.w("[$TAG] Model ${model.title} is already running for thread $threadId")
            return
        }

        Logger.i("[$TAG] Starting model: ${model.title}")

        // Add to starting models with initial progress
        startingModels.value = startingModels.value + (model to 0f)

        // Create a dedicated repository for this model
        val repository = llamaRepositoryFactory()

        // Create a child coroutine in the dedicated model execution scope
        val modelJob = modelExecutionScope.launch {
            try {
                executeModel(model, repository)
            } catch (e: Exception) {
                Logger.e("[$TAG] Model execution error for ${model.title}: ${e.message}")
                throw e
            } finally {
                // Clean up when model stops
                try {
                    repository.cleanUp()
                } catch (e: Exception) {
                    Logger.e("[$TAG] Error cleaning up model repository: ${e.message}")
                }
                runningModelExecutions.remove(executionKey)
                startingModels.value = startingModels.value - model
                updateRunningModelsState()
                Logger.i("[$TAG] Model ${model.title} execution completed for thread $threadId")
            }
        }

        // Track the job, model, and repository
        runningModelExecutions[executionKey] = ModelExecution(modelJob, model, repository)
        updateRunningModelsState()
    }

    suspend fun requestModelExecution(threadId: String, model: LocalModel): LlamaRepository? {
        val executionKey = ModelExecutionKey(threadId, model.id)

        // Check if this exact model+thread combination is already running
        val existingExecution = runningModelExecutions[executionKey]
        if (existingExecution != null) {
            Logger.d("[$TAG] Model ${model.id} is already running for thread $threadId, returning existing repository")
            return existingExecution.repository
        }

        // Check if model is currently starting (in startingModels)
        if (startingModels.value.containsKey(model)) {
            Logger.d("[$TAG] Model ${model.id} is starting, waiting for it to complete...")
            // Wait for the model to finish loading by monitoring startingModels
            while (startingModels.value.containsKey(model)) {
                kotlinx.coroutines.delay(100) // Check every 100ms
            }
            // Model should now be in runningModelExecutions
            return runningModelExecutions[executionKey]?.repository
        }

        // Model is not running for this thread, start it
        Logger.d("[$TAG] Model ${model.id} is not running for thread $threadId, starting it...")
        startModel(threadId, model)

        // Wait for the model to finish loading
        while (startingModels.value.containsKey(model)) {
            kotlinx.coroutines.delay(100) // Check every 100ms
        }

        // Return the repository after starting and loading is complete
        return runningModelExecutions[executionKey]?.repository
    }

    suspend fun stopModel(threadId: String, modelId: String) {
        val executionKey = ModelExecutionKey(threadId, modelId)
        val execution = runningModelExecutions[executionKey]
        if (execution == null) return

        Logger.i("[$TAG] Stopping model: $modelId for thread $threadId")
        execution.job.cancel()

        // Clean up the repository
        try {
            execution.repository.cleanUp()
        } catch (e: Exception) {
            Logger.e("[$TAG] Error cleaning up model repository: ${e.message}")
        }

        runningModelExecutions.remove(executionKey)
        updateRunningModelsState()
    }

    override suspend fun stopModel(modelId: String) {
        // Stop all instances of this model across all threads
        val executionsToStop = runningModelExecutions.filter { it.key.modelId == modelId }
        executionsToStop.forEach { (key, execution) ->
            Logger.i("[$TAG] Stopping model: $modelId for thread ${key.threadId}")
            execution.job.cancel()

            try {
                execution.repository.cleanUp()
            } catch (e: Exception) {
                Logger.e("[$TAG] Error cleaning up model repository: ${e.message}")
            }

            runningModelExecutions.remove(key)
        }
        updateRunningModelsState()
    }

    override suspend fun stopAllModels() {
        runningModelExecutions.values.forEach { execution ->
            execution.job.cancel()
            try {
                execution.repository.cleanUp()
            } catch (e: Exception) {
                Logger.e("[$TAG] Error cleaning up model repository: ${e.message}")
            }
        }

        runningModelExecutions.clear()
        updateRunningModelsState()
    }

    override fun flowOfRunningModels() = runningModels.asStateFlow()

    override fun flowOfStartingModels() = startingModels.asStateFlow()

    /**
     * Get the repository for a running model in a specific thread
     */
    fun getRepositoryForModel(threadId: String, modelId: String): LlamaRepository? {
        val executionKey = ModelExecutionKey(threadId, modelId)
        return runningModelExecutions[executionKey]?.repository
    }

    /**
     * Reload a model with new parameters. This will stop the current model and restart it.
     * Used when ModelLoadParams changes (which requires full model reload).
     */
    suspend fun reloadModelWithNewParams(threadId: String, model: LocalModel) {
        Logger.i("[$TAG] Reloading model ${model.title} with new parameters for thread $threadId")
        
        // Stop the current model
        stopModel(threadId, model.id)
        
        // Start the model again (will pick up new stored parameters)
        startModel(threadId, model)
    }

    override suspend fun reloadModel(threadId: String, model: LocalModel) {
        reloadModelWithNewParams(threadId, model)
    }

    private fun updateRunningModelsState() {
        runningModels.value = runningModelExecutions.values.map { it.model }.toSet()
    }

    /**
     * Actual model execution logic - initializes and loads the model
     * TODO: NamH the function name is misleading. we do not execute anything
     */
    private suspend fun executeModel(model: LocalModel, llamaRepository: LlamaRepository) {
        Logger.i("[$TAG] Executing model: ${model.title} at ${model.localPath}")

        // Initialize and load the model using LlamaRepository directly
        Logger.i("[$TAG] Initializing LlamaBinding and loading model: ${model.title}")
        llamaRepository.initialize()

        val path = model.localPath
        checkNotNull(path) { "Model path is null" }

        // Create progress callback
        val progressCallback = LlamaProgressCallback { progress ->
            Logger.d("[$TAG] Model ${model.title} loading progress: ${(progress * 100).toInt()}%")
            startingModels.value = startingModels.value + (model to progress)
            true // Continue loading
        }

        // Load stored parameters for this model, fallback to defaults if not found
        val modelLoadParams = try {
            modelLoadParamRepository.flowOfModelLoadParam(model.id).first()
        } catch (e: Exception) {
            Logger.w("[$TAG] Failed to load ModelLoadParams for ${model.id}, using defaults: ${e.message}")
            ModelLoadParams()
        }
        
        val modelContextParams = try {
            modelContextParamRepository.flowOfModelContextParam(model.id).first()
        } catch (e: Exception) {
            Logger.w("[$TAG] Failed to load ModelContextParams for ${model.id}, using defaults: ${e.message}")
            ModelContextParams()
        }

        Logger.d("[$TAG] Loading model ${model.title} with params:")
        Logger.d("[$TAG] - GPU Layers: ${modelLoadParams.nGpuLayers}")
        Logger.d("[$TAG] - Context Length: ${modelContextParams.nCtx}")

        llamaRepository.loadModel(
            modelPath = path,
            modelLoadParams = modelLoadParams,
            modelContextParams = modelContextParams,
            progressCallback = progressCallback
        )

        // Model finished loading, remove from starting models
        startingModels.value = startingModels.value - model

        Logger.i("[$TAG] Model ${model.title} is now ready for inference")

        // Keep the model "running" until cancelled
        while (true) {
            kotlinx.coroutines.delay(1000)
            // Model stays alive and ready for inference requests
        }
    }
}
