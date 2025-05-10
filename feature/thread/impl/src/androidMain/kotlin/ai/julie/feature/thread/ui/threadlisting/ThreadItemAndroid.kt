package ai.julie.feature.thread.ui.threadlisting

import ai.julie.feature.thread.domain.model.EnrichedThread
import androidx.compose.runtime.Composable

@Composable
actual fun ThreadItem(
    thread: EnrichedThread,
    isActive: Boolean,
    onThreadClicked: (String) -> Unit,
    onDeleteThread: (String) -> Unit,
) {
    ThreadItemContent(
        thread = thread,
        isActive = isActive,
        onThreadClicked = onThreadClicked
    )
}