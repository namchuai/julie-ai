package ai.julie.core.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface LoadTimeReporter<State> {
    fun bind(viewModel: ViewModel, flow: Flow<State>)
}

expect fun <State> doNotReportLoadTime(): LoadTimeReporter<State>

expect fun <State> reportLoadTimeWhen(
    isLoaded: (State) -> Boolean,
): LoadTimeReporter<State>