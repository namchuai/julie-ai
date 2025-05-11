package ai.julie.feature.chat

import ai.julie.core.designsystem.component.components.Icon
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.designsystem.component.components.textfield.TextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatInputSection(
    modifier: Modifier = Modifier,
    message: String,
    onMessageUpdate: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Message input box
        ChatInputField(
            value = message,
            onValueChange = onMessageUpdate,
            onSend = onSendMessage,
        )
    }
}

@Composable
fun ChatInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Message..") },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        )

        // Send button
        FloatingActionButton(
            onClick = onSend,
            containerColor = Color(0xFFBBDEFB),
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                Icons.Default.ArrowUpward,
                contentDescription = "Send",
                tint = Color.Blue
            )
        }
    }
}
