package ai.julie.feature.modelmanagement.ui.modelconfig

import ai.julie.core.common.AsyncState
import ai.julie.core.designsystem.component.components.Dropdown
import ai.julie.core.model.ModelLoadParams
import ai.julie.feature.modelconfig.domain.preset.SamplingPreset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun ModelConfigContainer() {
    val viewModel = koinInject<ModelConfigViewModel>()
    val state by viewModel.state.collectAsState()

    ModelConfigContent(
        state = state,
        onSamplingPresetSelected = viewModel::onPresetSelected,
        onContextLengthUpdate = viewModel::onContextLengthUpdate,
        onNBatchUpdate = viewModel::onNBatchUpdate,
        onNUBatchUpdate = viewModel::onNUBatchUpdate,
        onGpuLayersUpdate = viewModel::onGpuLayersUpdate,
        onUseMlockUpdate = viewModel::onUseMlockUpdate,
        onUseMmapUpdate = viewModel::onUseMmapUpdate,
        onReloadModel = viewModel::onReloadModel,
    )
}

@Composable
fun ModelConfigContent(
    state: ModelConfigState,
    onSamplingPresetSelected: (SamplingPreset) -> Unit,
    onContextLengthUpdate: (Float) -> Unit,
    onNBatchUpdate: (Float) -> Unit,
    onNUBatchUpdate: (Float) -> Unit,
    onGpuLayersUpdate: (Int) -> Unit,
    onUseMlockUpdate: (Boolean) -> Unit,
    onUseMmapUpdate: (Boolean) -> Unit,
    onReloadModel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (val samplingState = state.samplingPresetState) {
            is AsyncState.Success<SamplingPresetData> -> {
                Dropdown(
                    items = samplingState.value.samplingPresetList,
                    selectedItem = samplingState.value.selectedSamplingPreset,
                    onItemSelected = onSamplingPresetSelected,
                    itemLabel = { preset -> preset.name },
                    itemDescription = { preset -> preset.description },
                    label = "Sampling Preset",
                    placeholder = "Choose a sampling preset",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            else -> noOp()
        }

        when (val modelCtxState = state.modelContextState) {
            is AsyncState.Success<ModelContextData> -> {
                ModelContextContainer(
                    contextLength = modelCtxState.value.contextLength,
                    nBatch = modelCtxState.value.nBatch,
                    nUBatch = modelCtxState.value.nUBatch,
                    onContextLengthUpdate = onContextLengthUpdate,
                    onNBatchUpdate = onNBatchUpdate,
                    onNUBatchUpdate = onNUBatchUpdate,
                )
            }

            else -> noOp()
        }

        when (val modelLoadState = state.modelLoadState) {
            is AsyncState.Success<ModelLoadData> -> {
                ModelLoadParamsContainer(
                    modelLoadData = modelLoadState.value,
                    onGpuLayersUpdate = onGpuLayersUpdate,
                    onUseMlockUpdate = onUseMlockUpdate,
                    onUseMmapUpdate = onUseMmapUpdate,
                    onReloadModel = onReloadModel,
                )
            }

            else -> noOp()
        }
    }
}

@Composable
fun ModelLoadParamsContainer(
    modelLoadData: ModelLoadData,
    onGpuLayersUpdate: (Int) -> Unit,
    onUseMlockUpdate: (Boolean) -> Unit,
    onUseMmapUpdate: (Boolean) -> Unit,
    onReloadModel: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Model Load Parameters (Requires Reload)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        if (modelLoadData.pendingReload) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⚠️ Model Reload Required",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Model load parameters have changed. Click reload to apply changes.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = onReloadModel,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reload Model")
                    }
                }
            }
        }

        // GPU Layers Slider
        Column {
            Text(
                text = "GPU Layers: ${modelLoadData.currentParams.nGpuLayers}",
                style = MaterialTheme.typography.labelMedium
            )
            Slider(
                value = modelLoadData.currentParams.nGpuLayers.toFloat(),
                onValueChange = { onGpuLayersUpdate(it.toInt()) },
                valueRange = 0f..100f,
                steps = 99,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Use Mlock Checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Use Memory Lock (mlock)",
                style = MaterialTheme.typography.bodyMedium
            )
            Checkbox(
                checked = modelLoadData.currentParams.useMlock,
                onCheckedChange = onUseMlockUpdate
            )
        }

        // Use Mmap Checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Use Memory Mapping (mmap)",
                style = MaterialTheme.typography.bodyMedium
            )
            Checkbox(
                checked = modelLoadData.currentParams.useMmap,
                onCheckedChange = onUseMmapUpdate
            )
        }
    }
}

@Composable
private fun noOp() {}
