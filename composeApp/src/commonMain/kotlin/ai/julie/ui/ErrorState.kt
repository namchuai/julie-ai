package ai.julie.ui

import androidx.compose.runtime.compositionLocalOf

data class ErrorState(
    val showError: (String) -> Unit,
    val showModelNotFoundError: (String) -> Unit,
    val dismissError: () -> Unit
)

val LocalErrorState = compositionLocalOf<ErrorState> { 
    error("ErrorState not provided. Make sure to provide ErrorState at DesktopApp level") 
}