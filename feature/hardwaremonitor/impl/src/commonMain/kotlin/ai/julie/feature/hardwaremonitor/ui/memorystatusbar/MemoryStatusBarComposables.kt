package ai.julie.feature.hardwaremonitor.ui.memorystatusbar

import ai.julie.core.common.AsyncState
import ai.julie.core.designsystem.component.components.Text
import ai.julie.feature.hardwaremonitor.ui.common.UsageProgressBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MemoryStatusBarContainer() {
    val viewModel = koinViewModel<MemoryStatusBarViewModel>()
    val state by viewModel.state.collectAsState()

    MemoryStatusBarContent(
        state = state,
    )
}

@Composable
internal fun MemoryStatusBarContent(
    state: AsyncState<MemoryStatusBarState>,
) {
    when (state) {
        is AsyncState.Success -> {
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "RAM",
                    fontSize = 12.sp,
                )

                UsageProgressBar(state.value.percentage)
            }
        }

        else -> Unit
    }
}