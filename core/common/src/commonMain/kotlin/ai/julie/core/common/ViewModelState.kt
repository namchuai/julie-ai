package ai.julie.core.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
abstract class ViewModelState<State> internal constructor(
    internal val stateFlow: StateFlow<State>,
) : StateFlow<State> by stateFlow

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
class MutableViewModelState<State> internal constructor(
    private val mutableStateFlow: MutableStateFlow<State>,
) : ViewModelState<State>(mutableStateFlow), MutableStateFlow<State> by mutableStateFlow {

    override val replayCache: List<State> by mutableStateFlow::replayCache
    override var value: State by mutableStateFlow::value

    override suspend fun collect(collector: FlowCollector<State>): Nothing {
        stateFlow.collect(collector)
    }
}

fun <State : Any> ViewModel.viewModelState(
    savedStateBehaviour: SavedStateBehaviour<State>,
    loadTimeReporter: LoadTimeReporter<State>,
    initialState: State,
): MutableViewModelState<State> {
    val savedState = savedStateBehaviour.restore()
    val flow = MutableStateFlow(savedState ?: initialState)
    return MutableViewModelState(flow).also { viewModelState ->
        savedStateBehaviour.bind(state = viewModelState)

        // We're not going to report load times for ViewModels that use restored state,
        // so only bind the load time reporter if the saved state is null.
        if (savedState == null) {
            loadTimeReporter.bind(this@viewModelState, viewModelState)
        }
    }
}