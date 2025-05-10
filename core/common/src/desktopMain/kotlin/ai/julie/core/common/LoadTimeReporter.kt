package ai.julie.core.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface LoadTimeReporter<State> {
    actual fun bind(viewModel: ViewModel, flow: Flow<State>)
}

actual fun <State> doNotReportLoadTime(): LoadTimeReporter<State> {
    return object : LoadTimeReporter<State> {
        override fun bind(viewModel: ViewModel, flow: Flow<State>) = Unit
    }
}

actual fun <State> reportLoadTimeWhen(
    isLoaded: (State) -> Boolean,
): LoadTimeReporter<State> {
    return object : LoadTimeReporter<State> {
        override fun bind(viewModel: ViewModel, flow: Flow<State>) = Unit
    }
}