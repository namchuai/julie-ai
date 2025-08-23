package ai.julie.feature.thread.ui.threadlisting

import ai.julie.core.designsystem.component.AppTheme
import ai.julie.core.designsystem.component.components.Text
import ai.julie.feature.thread.domain.model.EnrichedThread
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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

@Composable
actual fun ThreadItem(
    thread: EnrichedThread,
    isActive: Boolean,
    onThreadClicked: (String) -> Unit,
    onDeleteThread: (String) -> Unit,
) {
    ContextMenuArea(
        items = {
            listOf(
                ContextMenuItem("Delete Thread") {
                    onDeleteThread(thread.id)
                }
            )
        }
    ) {
        ThreadItemContent(
            thread = thread,
            isActive = isActive,
            onThreadClicked = onThreadClicked
        )
    }
}

