package ai.julie.feature.thread.ui.threadlisting

import ai.julie.core.designsystem.component.AppTheme
import ai.julie.core.designsystem.component.components.Text
import ai.julie.feature.thread.domain.model.EnrichedThread
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
fun ThreadItemContent(
    thread: EnrichedThread,
    isActive: Boolean = false,
    onThreadClicked: (String) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val backgroundColor = when {
        isActive -> AppTheme.colors.primary.copy(alpha = 0.12f)
        isHovered -> AppTheme.colors.surface
        else -> AppTheme.colors.transparent
    }

    val textColor = if (isActive) AppTheme.colors.primary else AppTheme.colors.text
    val secondaryTextColor =
        if (isActive) AppTheme.colors.primary.copy(alpha = 0.7f) else AppTheme.colors.textSecondary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onThreadClicked(thread.id) }
            .hoverable(interactionSource)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thread icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (isActive) AppTheme.colors.primary else AppTheme.colors.outline,
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#",
                    color = if (isActive) AppTheme.colors.onPrimary else AppTheme.colors.onSurface,
                    style = AppTheme.typography.label1.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Thread ${thread.id.take(8)}",
                    color = textColor,
                    style = AppTheme.typography.body1.copy(
                        fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                val timeText = remember(thread.createdAt) {
                    formatTime(thread.createdAt)
                }

                Text(
                    text = timeText,
                    color = secondaryTextColor,
                    style = AppTheme.typography.body3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun formatTime(timestamp: Int): String {
    val instant = Instant.fromEpochSeconds(timestamp.toLong())
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.monthNumber}/${localDateTime.dayOfMonth}/${localDateTime.year}"
}