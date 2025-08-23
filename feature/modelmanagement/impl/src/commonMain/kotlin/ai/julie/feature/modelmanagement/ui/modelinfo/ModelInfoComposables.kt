package ai.julie.feature.modelmanagement.ui.modelinfo

import ai.julie.core.designsystem.component.components.Text
import ai.julie.feature.modelmanagement.ui.modelmetadata.ModelMetadataContainer
import ai.julie.feature.modelmanagement.ui.modelconfig.ModelConfigContainer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ModelInfoContainer() {
    val viewModel = koinViewModel<ModelInfoViewModel>()
    val state by viewModel.state.collectAsState()

    ModelInfoContent(
        state = state,
        onTabSelected = viewModel::onTabSelected
    )
}

@Composable
internal fun ModelInfoContent(
    state: ModelInfoState,
    onTabSelected: (ModelInfoTab) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Tab Row
        TabRow(
            selectedTab = state.selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.fillMaxWidth()
        )

        // Content based on selected tab
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (state.selectedTab) {
                ModelInfoTab.MODEL_SETTINGS -> {
                    ModelConfigContainer()
                }

                ModelInfoTab.MODEL_METADATA -> {
                    ModelMetadataContainer()
                }
            }
        }
    }
}

@Composable
private fun TabRow(
    selectedTab: ModelInfoTab,
    onTabSelected: (ModelInfoTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(16.dp)
    ) {
        TabItem(
            text = "Settings",
            selected = selectedTab == ModelInfoTab.MODEL_SETTINGS,
            onClick = { onTabSelected(ModelInfoTab.MODEL_SETTINGS) },
            modifier = Modifier.weight(1f)
        )
        TabItem(
            text = "Metadata",
            selected = selectedTab == ModelInfoTab.MODEL_METADATA,
            onClick = { onTabSelected(ModelInfoTab.MODEL_METADATA) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clickable { onClick() }
            .background(
                color = if (selected) Color.Blue.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.Blue else Color.Gray
        )
    }
}
