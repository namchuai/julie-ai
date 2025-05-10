package ai.julie.component.windowcontrols

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Window

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableTitleBar(
    modifier: Modifier = Modifier,
    window: Window? = null,
    content: @Composable RowScope.() -> Unit
) {
    var dragStartMousePos by remember { mutableStateOf<Point?>(null) }
    var dragStartWindowPos by remember { mutableStateOf<Point?>(null) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height = 40.dp)
            .pointerInput(window) {
                if (window != null) {
                    detectDragGestures(
                        onDragStart = {
                            dragStartMousePos = MouseInfo.getPointerInfo().location
                            dragStartWindowPos = window.location
                        },
                        onDrag = { _ ->
                            val currentMousePos = MouseInfo.getPointerInfo().location
                            val startMousePos = dragStartMousePos
                            val startWindowPos = dragStartWindowPos

                            if (startMousePos != null && startWindowPos != null) {
                                val deltaX = currentMousePos.x - startMousePos.x
                                val deltaY = currentMousePos.y - startMousePos.y

                                window.location = Point(
                                    startWindowPos.x + deltaX,
                                    startWindowPos.y + deltaY
                                )
                            }
                        }
                    )
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}