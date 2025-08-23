package ai.julie.component

import ai.julie.core.designsystem.component.AppTheme
import ai.julie.core.designsystem.component.LocalTypography
import ai.julie.core.designsystem.component.components.Text
import ai.julie.navigation.DesktopDestination
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun JulieLeftRibbon(
    selectedDestination: DesktopDestination,
    onDestinationSelected: (DesktopDestination) -> Unit,
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        DesktopDestination.entries.forEach { destination ->
            NavigationRailItem(
                destination = destination,
                selected = selectedDestination == destination,
                onClick = { onDestinationSelected(destination) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NavigationRailItem(
    destination: DesktopDestination,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val typography = LocalTypography.current

    Text(
        text = destination.title,
        style = typography.body2,
        fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
        color = if (selected) AppTheme.colors.onPrimary else AppTheme.colors.onSurface,
        modifier = Modifier.padding(12.dp).onClick {
            onClick()
        }
    )
}