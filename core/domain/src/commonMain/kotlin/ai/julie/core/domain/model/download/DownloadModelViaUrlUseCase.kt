package ai.julie.core.domain.model.download

import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import okio.buffer
import okio.use

// TODO: Inject HttpClient via constructor
class DownloadModelViaUrlUseCase(private val httpClient: HttpClient) {

    // Use default system filesystem. For testing, consider injecting FileSystem.
    private val fileSystem = FileSystem.SYSTEM

    operator fun invoke(
        url: String,
        destinationPath: String // Expecting full absolute path (e.g., from Android Context)
    ): Flow<DownloadInfo> = flow {
        emit(DownloadInfo.Downloading(0f))
        val destPath = destinationPath.toPath()
        var bytesSentTotal = 0L
        var success = false

        try {
            // Ensure parent directory exists
            destPath.parent?.let { parentDir ->
                fileSystem.createDirectories(parentDir, mustCreate = false)
            }

            httpClient.prepareGet(url).execute { response ->
                if (!response.status.isSuccess()) {
                    throw Exception("Download failed: ${response.status}")
                }

                val channel: ByteReadChannel = response.bodyAsChannel()
                val totalBytes = response.contentLength() ?: -1L

                // Use Okio sink for writing
                fileSystem.sink(destPath).buffer().use { sink ->
                    // Use readAvailable for robust reading
                    val buffer = ByteArray(8192) // 8KB buffer
                    while (!channel.isClosedForRead) {
                        val bytesRead = channel.readAvailable(buffer)
                        if (bytesRead == -1) break // End of stream
                        if (bytesRead > 0) {
                            sink.write(buffer, 0, bytesRead)
                            bytesSentTotal += bytesRead

                            if (totalBytes > 0) {
                                val progress = (bytesSentTotal * 1.0f / totalBytes).coerceIn(0f, 1f)
                                // Emit progress roughly every MB or at the end
                                if (bytesSentTotal % (1024 * 1024) == 0L || bytesSentTotal == totalBytes) {
                                    emit(DownloadInfo.Downloading(progress))
                                }
                            } else {
                                // Emit indeterminate progress if size is unknown
                                emit(DownloadInfo.Downloading(0f))
                            }
                        }
                    }
                    // Ensure all buffered data is written to the file
                    sink.flush()
                }

                if (totalBytes > 0 && bytesSentTotal != totalBytes) {
                    throw Exception("Download incomplete: Received $bytesSentTotal bytes, expected $totalBytes")
                }
                success = true
                emit(DownloadInfo.Success)
            }
        } catch (e: Exception) {
            emit(DownloadInfo.Error(e.message ?: "Unknown download error"))
        } finally {
            // Clean up partially downloaded file on failure
            if (!success && bytesSentTotal > 0) {
                try {
                    if (fileSystem.exists(destPath)) {
                        fileSystem.delete(destPath)
                    }
                } catch (cleanupEx: Exception) {
                    // Log cleanup exception if necessary, but prioritize original error
                    println("Failed to clean up partial download: ${cleanupEx.message}")
                }
            }
        }
    }.flowOn(Dispatchers.IO) // Perform network and file I/O on the IO dispatcher
}

// Helper function to check if status code represents success (2xx)
private fun io.ktor.http.HttpStatusCode.isSuccess(): Boolean = value in 200..299