package ai.julie.core.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect sealed class AsyncState<out T> {
    class Idle<T> : AsyncState<T>
    class Loading<T> : AsyncState<T>
    class Success<T> : AsyncState<T> {
        val value: T
    }

    class Failure<T> : AsyncState<T> {
        val throwable: Throwable
    }

    companion object
}

expect fun <T> createAsyncIdle(): AsyncState.Idle<T>
expect fun <T> createAsyncLoading(): AsyncState.Loading<T>
expect fun <T> createAsyncSuccess(value: T): AsyncState.Success<T>
expect fun <T> createAsyncFailure(throwable: Throwable): AsyncState.Failure<T>

fun <T> AsyncState<T>.getOrThrow(): T {
    return when (this) {
        is AsyncState.Success -> value
        is AsyncState.Failure -> throw throwable
        else -> throw IllegalStateException()
    }
}

fun <T> AsyncState<T>.getOrNull(): T? {
    return when (this) {
        is AsyncState.Success -> value
        else -> null
    }
}

fun <T> AsyncState.Companion.fromSuspending(block: suspend () -> T): Flow<AsyncState<T>> {
    return flow {
        emit(createAsyncLoading<T>())
        emit(createAsyncSuccess(block()))
    }.catch { emit(createAsyncFailure<T>(it)) }
}

fun <T> (suspend () -> T).asAsyncState(): Flow<AsyncState<T>> {
    return AsyncState.fromSuspending(this)
}

fun <T, R> AsyncState<T>.map(block: (T) -> R): AsyncState<R> {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        is AsyncState.Success -> createAsyncSuccess(block(value))
        else -> this as AsyncState<R>
    }
}

fun <T, R> AsyncState<List<T>>.mapContent(transform: (T) -> R): AsyncState<List<R>> {
    return this.map { it.map(transform) }
}