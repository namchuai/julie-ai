package ai.julie.feature.modelmanagement.ui.modelconfig.components

import ai.julie.core.designsystem.component.components.Slider
import ai.julie.core.designsystem.component.components.Text
import ai.julie.core.designsystem.component.components.textfield.TextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SliderComponent(
    key: String,
    min: Any,
    max: Any,
    step: Any,
    value: Any,
    onValueUpdate: (Any) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Convert all values to Float for the Slider component
    val (floatMin, floatMax, floatStep, floatValue) = when {
        value is Float && min is Float && max is Float && step is Float ->
            listOf(min, max, step, value)

        value is Int && min is Int && max is Int && step is Int ->
            listOf(min.toFloat(), max.toFloat(), step.toFloat(), value.toFloat())

        value is Double && min is Double && max is Double && step is Double ->
            listOf(min.toFloat(), max.toFloat(), step.toFloat(), value.toFloat())

        else -> throw IllegalArgumentException("SliderComponent requires all parameters to be the same numeric type (Float, Int, or Double)")
    }

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = key,
            )

            TextField(
                value = floatValue.toString(),
                onValueChange = { newValue ->
                    // Attempt to parse the new value as a Float
                    val parsedValue = newValue.toFloatOrNull()
                    if (parsedValue != null && parsedValue in floatMin..floatMax) {
                        onValueUpdate(parsedValue)
                    }
                },
                singleLine = true,
                modifier = Modifier.width(80.dp)
            )
        }

        Slider(
            value = floatValue,
            onValueChange = { newValue -> onValueUpdate(newValue) },
            valueRange = floatMin..floatMax,
            steps = if (floatStep > 0) ((floatMax - floatMin) / floatStep).toInt() else 0,
        )
    }
}
