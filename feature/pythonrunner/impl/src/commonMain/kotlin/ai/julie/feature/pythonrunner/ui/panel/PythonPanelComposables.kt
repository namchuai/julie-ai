package ai.julie.feature.pythonrunner.ui.panel

import ai.julie.core.designsystem.component.components.Button
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.designsystem.component.components.textfield.TextField
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PythonPanelContainer() {
    val viewModel = koinViewModel<PythonPanelViewModel>()
    val state by viewModel.state.collectAsState()

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .scrollable(scrollState, orientation = Orientation.Vertical)
            .fillMaxSize(),
    ) {

        Button(
            text = "Get installed packages",
            onClick = { viewModel.onGetInstalledPackagesClick() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            TextField(
                value = state.packageName,
                onValueChange = { viewModel.onPackageNameUpdate(it) },
                label = { Text("Package name") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                text = "Install Package",
                onClick = { viewModel.onInstallPackageClick() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Code Execution Section
        TextField(
            minLines = 4,
            value = state.command,
            onValueChange = { viewModel.onCommandUpdate(it) },
            label = { Text("Python Code") },
        )

        Button(
            text = "Execute Code",
            onClick = { viewModel.onExecuteCodeClick() }
        )
    }
}