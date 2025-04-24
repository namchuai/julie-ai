package ai.julie.feature.modelconfig.screen.modelconfig

import ai.julie.core.designsystem.component.components.Accordion
import ai.julie.core.designsystem.component.components.Slider
import ai.julie.core.designsystem.component.components.Switch
import ai.julie.core.designsystem.component.components.Text
import ai.julie.resources.Res
import ai.julie.resources.modelconfig_inference_settings
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource

@Composable
fun ModelConfigContent() {
    Accordion(
        headerContent = {
            Text(text = stringResource(Res.string.modelconfig_inference_settings))
        },
        bodyContent = {
            Slider(
                value = 0.5f,
                onValueChange = {},
            )
            Switch(
                checked = true,
                onCheckedChange = {},
            )
        },
    )
}