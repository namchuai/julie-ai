package ai.julie

import ai.julie.core.designsystem.component.AppTheme
import ai.julie.navigation.JulieNavGraph
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    AppTheme {
        KoinContext {
            JulieNavGraph()
        }
    }
}