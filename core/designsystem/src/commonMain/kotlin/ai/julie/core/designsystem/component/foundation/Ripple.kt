package ai.julie.core.designsystem.component.foundation

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.unit.Dp

/**
 * Design system wrapper around Material3 ripple for consistency.
 * This delegates to the Material3 ripple implementation.
 */
@Composable
fun ripple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: Color = Color.Unspecified,
): IndicationNodeFactory {
    return androidx.compose.material3.ripple(
        bounded = bounded,
        radius = radius,
        color = color
    )
}

@Composable
fun ripple(
    color: ColorProducer,
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
): IndicationNodeFactory {
    return androidx.compose.material3.ripple(
        color = color,
        bounded = bounded,
        radius = radius
    )
}

// Re-export Material3 ripple configuration for convenience
@OptIn(ExperimentalMaterial3Api::class)
val LocalRippleConfiguration: ProvidableCompositionLocal<androidx.compose.material3.RippleConfiguration?> =
    androidx.compose.material3.LocalRippleConfiguration