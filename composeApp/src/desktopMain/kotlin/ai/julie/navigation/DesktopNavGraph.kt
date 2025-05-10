package ai.julie.navigation

import ai.julie.component.JulieLeftRibbon
import ai.julie.feature.pythonrunner.ui.panel.PythonPanelContainer
import ai.julie.panel.chat.ChatPanel
import ai.julie.panel.modelmanagement.ModelManagementPanel
import ai.julie.panel.promptlab.PromptLabPanel
import ai.julie.panel.setting.SettingPanel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class DesktopDestination(val title: String) {
    Chat("Chat"),
    PromptLab("Prompt Lab"),
    ModelManagement("Models"),
    Setting("Setting"),
    Python("Python");
}

@Composable
fun DesktopNavGraph() {
    var selectedDestination by remember { mutableStateOf(DesktopDestination.Chat) }

    Row(modifier = Modifier.fillMaxSize()) {
        JulieLeftRibbon(
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1D1C1D))
        ) {
            when (selectedDestination) {
                DesktopDestination.Chat -> ChatPanel()
                DesktopDestination.PromptLab -> PromptLabPanel()
                DesktopDestination.ModelManagement -> ModelManagementPanel(
                    onNavigateToChat = { selectedDestination = DesktopDestination.Chat }
                )
                DesktopDestination.Setting -> SettingPanel()
                DesktopDestination.Python -> PythonPanelContainer()
            }
        }
    }
}
