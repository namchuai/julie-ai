package ai.julie.feature.thread.ui.threadlisting

import ai.julie.core.common.AsyncState
import ai.julie.core.designsystem.component.AppTheme
import ai.julie.core.designsystem.component.components.Text
import ai.julie.feature.thread.domain.model.EnrichedThread
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ThreadListingScreenRoute(
    onBackClick: () -> Unit = {},
) {
    val viewModel = koinViewModel<ThreadListingViewModel>()
    val state by viewModel.state.collectAsState()

    ThreadListingContent(
        state = state,
        onThreadClick = viewModel::onThreadSelected,
        onDeleteThread = viewModel::onDeleteThread,
    )
}

@Composable
fun ThreadListingContent(
    state: AsyncState<ThreadListingState>,
    onThreadClick: (String) -> Unit,
    onDeleteThread: (String) -> Unit,
) {
    when (state) {
        is AsyncState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = AppTheme.colors.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        is AsyncState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(state.value.threads.size) { index ->
                    val thread = state.value.threads[index]
                    val isActive = state.value.activeThread?.id == thread.id
                    ThreadItem(
                        thread = thread,
                        isActive = isActive,
                        onThreadClicked = onThreadClick,
                        onDeleteThread = onDeleteThread
                    )
                }
            }
        }

        is AsyncState.Failure -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error loading threads: ${state.throwable.message}")
            }
        }

        else -> noOp()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun noOp() = Unit

@Composable
expect fun ThreadItem(
    thread: EnrichedThread,
    isActive: Boolean = false,
    onThreadClicked: (String) -> Unit,
    onDeleteThread: (String) -> Unit,
)

