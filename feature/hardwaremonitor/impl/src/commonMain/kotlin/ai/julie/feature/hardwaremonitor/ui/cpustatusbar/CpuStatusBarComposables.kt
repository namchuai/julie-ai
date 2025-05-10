package ai.julie.feature.hardwaremonitor.ui.cpustatusbar

import ai.julie.core.common.AsyncState
import ai.julie.core.designsystem.component.components.Text
import ai.julie.feature.hardwaremonitor.ui.common.UsageProgressBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CpuStatusBarContainer() {
    val viewModel = koinViewModel<CpuStatusBarViewModel>()
    val state by viewModel.state.collectAsState()

    CpuStatusBarContent(
        state = state,
    )
}

@Composable
fun CpuStatusBarContent(
    state: AsyncState<CpuStatusBarState>,
) {
    when (state) {
        is AsyncState.Success -> {
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "CPU",
                    fontSize = 12.sp,
                )

                UsageProgressBar(state.value.percentage)
            }
        }

        else -> Unit
    }
}