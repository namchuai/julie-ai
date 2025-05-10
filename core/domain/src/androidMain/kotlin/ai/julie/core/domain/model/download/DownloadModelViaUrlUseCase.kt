package ai.julie.core.domain.model.download

import ai.julie.logging.Logger
import android.content.Context
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.writeBytes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.readRemaining
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DownloadModelViaUrlUseCase actual constructor() : KoinComponent {
    private val httpClient: HttpClient by inject()
    private val context: Context by inject()

    actual suspend operator fun invoke(url: String) {
        try {
            Logger.d { "Starting download from $url" }
            val fileName = url.substringAfterLast("/")

            val modelsDir = PlatformFile("${context.filesDir}/models")
            val destinationFile = PlatformFile("${context.filesDir}/models/$fileName")
            Logger.d { "Destination path: ${destinationFile.path}" }

            if (!modelsDir.exists()) {
                Logger.d { "Creating models directory" }
                modelsDir.createDirectories()
            }

            val allBytes = mutableListOf<ByteArray>()
            httpClient.prepareGet(url).execute { httpResponse ->
                val channel: ByteReadChannel = httpResponse.body()
                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                    while (!packet.isEmpty) {
                        allBytes.add(packet.readBytes())
                    }
                }
            }
            
            // Combine all bytes and write at once
            val totalBytes = allBytes.flatMap { it.toList() }.toByteArray()
            destinationFile.writeBytes(totalBytes)
            Logger.d { "Download successfully completed! Destination: ${destinationFile.path}" }
        } catch (e: Exception) {
            Logger.e { "Download failed for $url: ${e.message}" }
        }
    }
}