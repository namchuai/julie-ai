package ai.julie.core.domain.model.download

import android.os.Build
import androidx.annotation.RequiresApi
import okio.Path
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import okio.Path.Companion.toPath

@RequiresApi(Build.VERSION_CODES.O)
actual fun getAppStoragePath(): Path {
    val context = object : KoinComponent {
        val ctx: android.content.Context by inject()
    }.ctx

    // Correctly use the toPath extension function
    return context.filesDir.absolutePath.toPath()
}
