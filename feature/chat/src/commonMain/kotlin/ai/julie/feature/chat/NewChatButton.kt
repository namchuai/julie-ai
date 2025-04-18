package ai.julie.feature.chat

import ai.julie.core.designsystem.component.components.Button
import ai.julie.core.designsystem.component.components.ButtonVariant
import ai.julie.core.designsystem.component.components.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NewChatButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Button(
            variant = ButtonVariant.PrimaryOutlined,
            onClick = onClick,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("New chat")
        }
    }
}
