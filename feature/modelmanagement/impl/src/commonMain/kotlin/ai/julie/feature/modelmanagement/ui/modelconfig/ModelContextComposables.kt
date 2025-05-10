package ai.julie.feature.modelmanagement.ui.modelconfig

import ai.julie.core.designsystem.component.components.Slider
import ai.julie.core.designsystem.component.components.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun ModelContextContainer(
    contextLength: ContextLengthParam,
    nBatch: ContextLengthParam,
    nUBatch: ContextLengthParam,
    onContextLengthUpdate: (Float) -> Unit,
    onNBatchUpdate: (Float) -> Unit,
    onNUBatchUpdate: (Float) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SliderComponent(
            title = "Context Length",
            min = contextLength.min.toFloat(),
            max = contextLength.max.toFloat(),
            step = contextLength.step,
            value = contextLength.value.toFloat(),
            onValueChange = { onContextLengthUpdate(it) }
        )

        SliderComponent(
            title = "# Batches",
            min = nBatch.min.toFloat(),
            max = nBatch.max.toFloat(),
            step = nBatch.step,
            value = nBatch.value.toFloat(),
            onValueChange = { onNBatchUpdate(it) }
        )

        SliderComponent(
            title = "# μBatch",
            min = nUBatch.min.toFloat(),
            max = nUBatch.max.toFloat(),
            step = nUBatch.step,
            value = nUBatch.value.toFloat(),
            onValueChange = { onNUBatchUpdate(it) }
        )
    }
}

@Composable
private fun SliderComponent(
    title: String,
    min: Float,
    max: Float,
    step: Int,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = value.toString(),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }


        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            steps = step,
            modifier = Modifier.fillMaxWidth()
        )
    }
}