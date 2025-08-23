package ai.julie.feature.chat

import ai.julie.core.designsystem.component.AppTheme
import ai.julie.core.designsystem.component.components.Text
import ai.julie.feature.message.domain.model.EnrichedMessage
import ai.julie.feature.message.domain.model.EnrichedRole
import ai.julie.feature.message.domain.model.extractTextContent
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography

@Composable
fun ChatMessageList(
    messages: List<EnrichedMessage>,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        items(items = messages) { message ->
            // TODO: handle more rendering type of role
            if (message.role == EnrichedRole.User) {
                UserMessageItem(message = message)
            } else {
                AssistantMessageItem(message = message)
            }
        }
    }
}

@Composable
fun UserMessageItem(
    message: EnrichedMessage,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 4.dp
                    )
                )
                .background(AppTheme.colors.primary)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            SelectionContainer {
                Text(
                    text = message.extractTextContent(),
                    color = AppTheme.colors.onPrimary,
                    style = AppTheme.typography.body1
                )
            }
        }
    }
}

@Composable
fun AssistantMessageItem(message: EnrichedMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = AppTheme.colors.surface,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AI",
                color = AppTheme.colors.primary,
                style = AppTheme.typography.label2.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Message bubble with markdown
        Column(
            modifier = Modifier.weight(1f)
        ) {
            SelectionContainer {
                Box(
                    modifier = Modifier
                        .widthIn(max = 320.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 18.dp,
                                bottomStart = 18.dp,
                                bottomEnd = 18.dp
                            )
                        )
                        .background(AppTheme.colors.surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Markdown(
                        content = message.extractTextContent(),
                        colors = markdownColor(
                            text = AppTheme.colors.text,
                            codeText = AppTheme.colors.text,
                            linkText = AppTheme.colors.primary,
                            codeBackground = AppTheme.colors.outline.copy(alpha = 0.1f),
                            dividerColor = AppTheme.colors.outline
                        ),
                        typography = markdownTypography(
                            h1 = AppTheme.typography.h1,
                            h2 = AppTheme.typography.h2,
                            h3 = AppTheme.typography.h3,
                            h4 = AppTheme.typography.h4,
                            h5 = AppTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                            h6 = AppTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                            text = AppTheme.typography.body1,
                            code = AppTheme.typography.body1.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                            quote = AppTheme.typography.body1.copy(fontWeight = FontWeight.Medium)
                        )
                    )
                }
            }
        }
    }
}
