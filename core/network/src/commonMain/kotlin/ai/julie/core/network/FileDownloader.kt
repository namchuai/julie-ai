package ai.julie.core.network

import ai.julie.logging.Logger
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.write
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * File download progress callback
 */
data class FileDownloadProgress(
    val downloadedBytes: Long,
    val totalBytes: Long,
    val percentage: Float = if (totalBytes > 0) (downloadedBytes.toFloat() / totalBytes) * 100f else 0f
)

/**
 * File downloader using Ktor HttpClient
 */
class FileDownloader(
    private val httpClient: HttpClient
) {
    /**
     * Download file content as ByteArray
     * @param url URL to download from
     * @return Downloaded content as ByteArray
     */
    suspend fun downloadFileContent(url: String): ByteArray {
        Logger.d("Downloading file content from: $url")

        val response: HttpResponse = httpClient.get(url)

        if (!response.status.isSuccess()) {
            throw Exception("Download failed with status: ${response.status}")
        }

        val channel: ByteReadChannel = response.bodyAsChannel()
        val packets = mutableListOf<ByteArray>()

        while (!channel.isClosedForRead) {
            val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
            if (packet.isEmpty) break
            packets.add(packet.readBytes())
        }

        val totalSize = packets.sumOf { it.size }
        val result = ByteArray(totalSize)
        var offset = 0

        for (packet in packets) {
            packet.copyInto(result, offset)
            offset += packet.size
        }

        Logger.d("Downloaded ${result.size} bytes from $url")
        return result
    }

    /**
     * Download a file from URL to destination
     * @param url URL to download from
     * @param destinationFile Destination file
     * @return Flow of download progress
     */
    suspend fun downloadFile(
        url: String,
        destinationFile: PlatformFile
    ): Flow<FileDownloadProgress> = flow {
        try {
            Logger.d("Starting download: $url -> ${destinationFile.path}")

            val response: HttpResponse = httpClient.get(url)

            if (!response.status.isSuccess()) {
                throw Exception("Download failed with status: ${response.status}")
            }

            val contentLength = response.headers["Content-Length"]?.toLongOrNull() ?: -1L
            val channel: ByteReadChannel = response.bodyAsChannel()

            // Download to memory first
            val packets = mutableListOf<ByteArray>()
            var totalBytesRead = 0L

            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                if (packet.isEmpty) break

                val bytes = packet.readBytes()
                packets.add(bytes)
                totalBytesRead += bytes.size

                // Emit progress
                val progress = FileDownloadProgress(
                    downloadedBytes = totalBytesRead,
                    totalBytes = contentLength
                )
                emit(progress)
            }

            // Combine all packets into final byte array
            val totalSize = packets.sumOf { it.size }
            val result = ByteArray(totalSize)
            var offset = 0

            for (packet in packets) {
                packet.copyInto(result, offset)
                offset += packet.size
            }

            // Write to file using FileKit
            destinationFile.write(result)
            Logger.d("Download completed: ${destinationFile.path}")

        } catch (e: Exception) {
//            Logger.e("Download failed: $url", e)
            throw e
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 8192
    }
}