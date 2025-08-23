package ai.julie.core.designsystem.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * A reusable dropdown component that displays a list of selectable items
 */
@Composable
fun <T> Dropdown(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    itemDescription: ((T) -> String)? = null,
    label: String? = null,
    placeholder: String = "Select an option",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Label
        label?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // Dropdown Box
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = if (enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(
                        color = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(enabled = enabled) { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = itemLabel(selectedItem),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (enabled) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    itemDescription?.invoke(selectedItem)?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = if (enabled) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(300.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = itemLabel(item),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (item == selectedItem) FontWeight.Medium else FontWeight.Normal
                                )
                                itemDescription?.invoke(item)?.let { description ->
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        modifier = Modifier
                            .background(
                                if (item == selectedItem)
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                                else Color.Transparent
                            )
                    )
                }
            }
        }
    }
}