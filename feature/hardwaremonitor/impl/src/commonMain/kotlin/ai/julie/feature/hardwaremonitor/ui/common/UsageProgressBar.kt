package ai.julie.feature.hardwaremonitor.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun UsageProgressBar(
    percentage: Double,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(14.dp)
            .border(1.dp, Color.Gray)
            .padding(2.dp)
    ) {
        // Background (empty part)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(16.dp)
                .background(Color.DarkGray)
        )

        // Filled part (from bottom to top)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .width(16.dp)
                .fillMaxHeight(percentage.toFloat() / 100f)
                .background(Color.White)
        )
    }
}
