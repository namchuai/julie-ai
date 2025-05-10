package ai.julie.ui.bottomstatusbar

import ai.julie.core.common.AsyncState
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.model.aimodel.LocalModel
import ai.julie.feature.hardwaremonitor.ui.cpustatusbar.CpuStatusBarContainer
import ai.julie.feature.hardwaremonitor.ui.memorystatusbar.MemoryStatusBarContainer
import ai.julie.feature.thread.ui.threadlisting.noOp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BottomStatusBar() {
    val viewModel = koinViewModel<BottomStatusBarViewModel>()
    val state by viewModel.state.collectAsState()

    BottomStatusBarContent(
        state = state,
    )
}

@Composable
internal fun BottomStatusBarContent(
    state: AsyncState<BottomStatusBarState>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        when (state) {
            is AsyncState.Success -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ModelStatus(
                        startingModels = state.value.startingModels,
                        runningModels = state.value.runningModels,
                    )

                    CpuStatusBarContainer()

                    MemoryStatusBarContainer()

                    Text(
                        text = "v${state.value.appVersion} (${state.value.buildNo})",
                        fontSize = 12.sp,
                    )
                }
            }

            else -> noOp()
        }
    }
}

@Composable
internal fun ModelStatus(
    startingModels: Map<LocalModel, Float>,
    runningModels: Set<LocalModel>,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (startingModels.isNotEmpty()) {
            // priority starting models over running models
            startingModels.entries.firstOrNull()?.let { (model, progress) ->
                ModelLoadingProgress(
                    modelTitle = model.title,
                    progress = progress
                )
            }
        } else if (runningModels.isNotEmpty()) {
            val runningText = if (runningModels.size == 1) {
                runningModels.first().title
            } else {
                "${runningModels.size} models loaded"
            }
            Text(
                text = runningText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
internal fun ModelLoadingProgress(
    modelTitle: String,
    progress: Float,
) {
    Text(
        text = "Loading $modelTitle",
        fontSize = 12.sp,
    )
    Spacer(modifier = Modifier.width(8.dp))
    Box(modifier = Modifier.width(100.dp).height(4.dp)) {
        // Background progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color.Gray.copy(alpha = 0.3f))
        )
        // Progress fill
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(4.dp)
                .background(Color.Blue)
        )
    }
    Spacer(modifier = Modifier.width(8.dp))
    Text(
        text = "${(progress * 100).toInt()}%",
        fontSize = 12.sp,
    )
}
