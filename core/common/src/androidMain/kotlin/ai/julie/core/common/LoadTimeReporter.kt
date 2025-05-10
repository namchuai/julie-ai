package ai.julie.core.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.takeWhile

actual interface LoadTimeReporter<State> {
    actual fun bind(viewModel: ViewModel, flow: Flow<State>)

    companion object {
        const val LOAD_TIME_METRIC_NAME = "view_model_load_time"
    }
}

actual fun <State> doNotReportLoadTime(): LoadTimeReporter<State> {
    return object : LoadTimeReporter<State> {
        override fun bind(viewModel: ViewModel, flow: Flow<State>) = Unit
    }
}

actual fun <State> reportLoadTimeWhen(
    isLoaded: (State) -> Boolean,
): LoadTimeReporter<State> {
    val started = System.currentTimeMillis()
    return object : LoadTimeReporter<State> {
        override fun bind(
            viewModel: ViewModel,
            flow: Flow<State>
        ) {
            flow
                .takeWhile { !isLoaded(it) }
                .onCompletion {
                    val durationMs = System.currentTimeMillis() - started
                    // TODO: put the log here or can incoporate some logging service
                }.launchIn(viewModel.viewModelScope)
        }
    }
}
