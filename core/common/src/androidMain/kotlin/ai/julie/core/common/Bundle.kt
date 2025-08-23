package ai.julie.core.common

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.BundleCompat

inline fun <reified T : Parcelable> Bundle.getParcelableCompat(key: String): T? =
    BundleCompat.getParcelable(this, key, T::class.java)

fun Bundle.getIntOrNull(key: String): Int? = if (containsKey(key)) getInt(key) else null