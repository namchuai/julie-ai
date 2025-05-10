package ai.julie.core.domain.model.download

import ai.julie.core.network.FileDownloader
import ai.julie.logging.Logger
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.flow.last
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DownloadModelViaUrlUseCase actual constructor() : KoinComponent {
    private val fileDownloader: FileDownloader by inject()

    actual suspend operator fun invoke(url: String) {
        try {
            Logger.d { "Starting download from $url" }
            val fileName = url.substringAfterLast("/")

            // For desktop, we'll use a default models directory in user home
            val userHome = System.getProperty("user.home")
            val modelsDir = "$userHome/.julie/models"
            val destinationFile = PlatformFile("$modelsDir/$fileName")

            Logger.d { "Destination file: ${destinationFile.path}" }

            // Use FileDownloader that provides progress
            fileDownloader.downloadFile(url, destinationFile).last()

            Logger.d { "Download successfully completed! File: ${destinationFile.path}" }
        } catch (e: Exception) {
            Logger.e { "Download failed for $url: ${e.message}" }
        }
    }
}