package ai.julie.core.common

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface SavedStateBehaviour<T : Any> {
    fun bind(state: ViewModelState<T>)
    fun restore(): T?
}

expect fun <T : Any> doNotSaveState(): SavedStateBehaviour<T>
