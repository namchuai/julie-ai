package ai.julie.core.common

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface SavedStateBehaviour<T : Any> {
    actual fun bind(state: ViewModelState<T>)
    actual fun restore(): T?
}

actual fun <T : Any> doNotSaveState(): SavedStateBehaviour<T> = object : SavedStateBehaviour<T> {
    override fun bind(state: ViewModelState<T>) = Unit
    override fun restore(): T? = null
}