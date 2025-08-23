package ai.julie.core.common

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle

actual interface SavedStateBehaviour<T : Any> {
    actual fun bind(state: ViewModelState<T>)

    actual fun restore(): T?
}

actual fun <T : Any> doNotSaveState(): SavedStateBehaviour<T> = object : SavedStateBehaviour<T> {
    override fun bind(state: ViewModelState<T>) = Unit

    override fun restore(): T? = null
}

inline fun <reified T : Parcelable> saveState(
    savedStateHandle: SavedStateHandle,
): SavedStateBehaviour<T> = object : SavedStateBehaviour<T> {
    private val SAVED_STATE_PROVIDER_KEY = "SavedStateBehaviour.SAVED_STATE_PROVIDER"
    private val SAVED_STATE_KEY = "SavedStateBehaviour.SAVED_STATE"

    override fun bind(state: ViewModelState<T>) {
        savedStateHandle.setSavedStateProvider(SAVED_STATE_PROVIDER_KEY) {
            bundleOf(SAVED_STATE_KEY to state.value)
        }
    }

    override fun restore(): T? {
        val bundle = savedStateHandle.get<Bundle>(SAVED_STATE_PROVIDER_KEY)
        return bundle?.getParcelableCompat(SAVED_STATE_KEY)
    }
}
