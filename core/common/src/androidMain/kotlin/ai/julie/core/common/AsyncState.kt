@file:JvmName("AsyncStateAndroid")

package ai.julie.core.common

import android.os.Parcel
import android.os.Parcelable
import kotlinx.coroutines.CancellationException
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Parcelize
actual sealed class AsyncState<out T> : Parcelable {
    actual class Idle<T> : AsyncState<T>()

    actual class Loading<T> : AsyncState<T>()

    @Parcelize
    actual data class Success<T>(actual val value: T) : AsyncState<T>() {
        override fun toString(): String {
            return super.toString()
        }

        companion object : Parceler<Success<*>> {
            override fun Success<*>.write(parcel: Parcel, flags: Int) {
                val parcelClassName = value?.let { it::class.java.name } ?: "null"
                parcel.writeString("AsyncState(${parcelClassName})")
                when (parcelClassName) {
                    Unit::class.java.name, "null" -> {
                        // nothing
                    }

                    else -> {
                        parcel.writeValue(value)
                    }
                }
            }

            override fun create(parcel: Parcel): Success<*> {
                val parcelClassName = parcel.readString()
                return if (parcelClassName == null) {
                    throw IllegalStateException("Unable to create AsyncState.Success from parcel")
                } else {
                    when (val className =
                        parcelClassName.removePrefix("AsyncState(").removeSuffix(")")) {
                        "null" -> Success(null)
                        Unit::class.java.name -> Success(Unit)
                        else -> {
                            Success(parcel.readValue(Class.forName(className).classLoader))
                        }
                    }
                }
            }
        }
    }

    @Parcelize
    actual data class Failure<T>(actual val throwable: Throwable) : AsyncState<T>() {
        init {
            if (throwable is CancellationException) throw throwable
        }
    }

    override fun toString(): String {
        return when (this) {
            is Idle -> "AsyncState.Idle"
            is Loading -> "AsyncState.Loading"
            is Success -> "AsyncState.Success($value)"
            is Failure -> "AsyncState.Failure(${throwable::class.java.simpleName})"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    actual companion object
}

actual fun <T> createAsyncIdle(): AsyncState.Idle<T> = AsyncState.Idle()
actual fun <T> createAsyncLoading(): AsyncState.Loading<T> = AsyncState.Loading()
actual fun <T> createAsyncSuccess(value: T): AsyncState.Success<T> = AsyncState.Success(value)
actual fun <T> createAsyncFailure(throwable: Throwable): AsyncState.Failure<T> =
    AsyncState.Failure(throwable)
