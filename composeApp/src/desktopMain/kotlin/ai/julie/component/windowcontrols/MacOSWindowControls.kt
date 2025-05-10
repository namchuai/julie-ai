package ai.julie.component.windowcontrols

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import java.awt.Frame
import java.awt.Window
import kotlin.system.exitProcess

@Composable
fun MacOSWindowControls(
    modifier: Modifier = Modifier,
    window: Window? = null,
    onClose: (() -> Unit)? = null,
    onMinimize: (() -> Unit)? = null,
    onMaximize: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.padding(start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Close button (red)
        WindowControlButton(
            color = Color(0xFFFF5F57),
            icon = Icons.Default.Close,
            onClick = {
                onClose?.invoke() ?: run {
                    window?.dispose()
                    exitProcess(0)
                }
            }
        )

        // Minimize button (yellow)
        WindowControlButton(
            color = Color(0xFFFFBD2E),
            icon = Icons.Default.Remove,
            onClick = {
                onMinimize?.invoke() ?: run {
                    if (window is Frame) {
                        window.state = Frame.ICONIFIED
                    }
                }
            }
        )

        // Maximize button (green)
        WindowControlButton(
            color = Color(0xFF28CA42),
            icon = Icons.Default.CropSquare,
            onClick = {
                onMaximize?.invoke() ?: run {
                    if (window is Frame) {
                        window.state = if (window.state == Frame.MAXIMIZED_BOTH) {
                            Frame.NORMAL
                        } else {
                            Frame.MAXIMIZED_BOTH
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun WindowControlButton(
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        modifier = modifier
            .size(14.dp)
            .background(
                color = color,
                shape = CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isHovered) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(8.dp),
                tint = Color.Black.copy(alpha = 0.6f)
            )
        }
    }
}
