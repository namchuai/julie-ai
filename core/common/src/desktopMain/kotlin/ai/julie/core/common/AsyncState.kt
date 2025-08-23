@file:JvmName("AsyncStateDesktop")

package ai.julie.core.common

import kotlinx.coroutines.CancellationException

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual sealed class AsyncState<out T> {
    actual class Idle<T> : AsyncState<T>()

    actual class Loading<T> : AsyncState<T>()

    actual data class Success<T>(actual val value: T) : AsyncState<T>()

    actual data class Failure<T>(actual val throwable: Throwable) : AsyncState<T>() {
        init {
            if (throwable is CancellationException) throw throwable
        }
    }

    actual companion object
}

actual fun <T> createAsyncIdle(): AsyncState.Idle<T> = AsyncState.Idle()
actual fun <T> createAsyncLoading(): AsyncState.Loading<T> = AsyncState.Loading()
actual fun <T> createAsyncSuccess(value: T): AsyncState.Success<T> = AsyncState.Success(value)
actual fun <T> createAsyncFailure(throwable: Throwable): AsyncState.Failure<T> =
    AsyncState.Failure(throwable)