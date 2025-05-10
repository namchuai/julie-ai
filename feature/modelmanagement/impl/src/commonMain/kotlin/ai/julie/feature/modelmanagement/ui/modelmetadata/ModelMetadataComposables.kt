package ai.julie.feature.modelmanagement.ui.modelmetadata

import ai.julie.core.common.AsyncState
import ai.julie.core.designsystem.component.components.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ModelMetadataContainer() {
    val viewModel = koinViewModel<ModelMetadataViewModel>()
    val state by viewModel.state.collectAsState()

    ModelMetadataContent(state)
}

@Composable
internal fun ModelMetadataContent(
    state: AsyncState<ModelMetadataState>
) {
    when (state) {
        is AsyncState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Loading model metadata...")
            }
        }

        is AsyncState.Success -> {
            MetadataTable(state.value.metadataItems)
        }

        else -> noOp()
    }
}

@Composable
private fun MetadataTable(metadataItems: List<MetadataItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray.copy(alpha = 0.2f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Key",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.4f)
                )
                Text(
                    text = "Value",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.6f)
                )
            }
        }

        items(metadataItems) { metadataItem ->
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    when (metadataItem) {
                        is MetadataItem.PlainText -> {
                            Text(
                                text = metadataItem.key,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(0.4f)
                            )
                            Text(
                                text = metadataItem.value,
                                modifier = Modifier.weight(0.6f)
                            )
                        }

                        is MetadataItem.MultilineText -> {
                            val clipboardManager = LocalClipboardManager.current
                            Text(
                                text = metadataItem.key,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(0.4f)
                            )
                            Row(
                                modifier = Modifier.weight(0.6f),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = metadataItem.value,
                                    maxLines = 5,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(metadataItem.value))
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy to clipboard",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }


                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray.copy(alpha = 0.1f))
                )
            }
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun noOp() = Unit
