package ai.julie.feature.chat

import ai.julie.core.designsystem.component.components.Icon
import ai.julie.core.designsystem.component.components.IconButton
import ai.julie.core.designsystem.component.components.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatMessageList(
    modifier: Modifier = Modifier,
    messages: List<MessageItem>,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(messages) { message ->
            if (message.isFromUser) {
                UserMessageItem(message = message)
            } else {
                AssistantMessageItem(message = message)
            }
        }
    }
}

@Composable
fun UserMessageItem(
    modifier: Modifier = Modifier,
    message: MessageItem,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 64.dp)
    ) {
        Text(
            text = message.content,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE3F2FD))
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
fun AssistantMessageItem(message: MessageItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Assistant avatar
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFE1F5FE)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Assistant,
                contentDescription = "Assistant",
                tint = Color.Blue
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Message content
        Column {
            Text(
                text = message.content,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Reaction buttons
            MessageReactionButtons()
        }
    }
}

@Composable
fun MessageReactionButtons() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { /* Handle copy */ },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Outlined.ContentCopy,
                contentDescription = "Copy",
                modifier = Modifier.size(18.dp)
            )
        }

        IconButton(
            onClick = { /* Handle regenerate */ },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Outlined.Refresh,
                contentDescription = "Regenerate",
                modifier = Modifier.size(18.dp)
            )
        }

        IconButton(
            onClick = { /* Handle like */ },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Outlined.ThumbUp,
                contentDescription = "Like",
                modifier = Modifier.size(18.dp)
            )
        }

        IconButton(
            onClick = { /* Handle dislike */ },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Outlined.ThumbDown,
                contentDescription = "Dislike",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
