package ai.julie.core.fs

import ai.julie.core.fs.model.FileModel
import ai.julie.core.fs.model.FileType
import ai.julie.core.fs.model.ListFileOpts
import ai.julie.core.fs.model.getDirName
import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FileSystem(
    context: Context,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
) {

    // Use app's internal files directory as the default writable path.
    private val defaultWritablePath: String = context.filesDir.absolutePath
    private var currentWritablePath: String = defaultWritablePath

    actual fun getWritablePath() = currentWritablePath

    actual fun setWritablePath(path: String): Boolean =
        throw UnsupportedOperationException("setWritablePath is not supported on Android.")

    actual suspend fun write(relativePath: String, data: ByteArray) {
        withContext(dispatchers) {
//            val file = resolvePath(relativePath)
//            try {
//                file.parentFile?.mkdirs()
//                FileOutputStream(file).use { fos ->
//                    fos.write(data)
//                }
//            } catch (e: SecurityException) {
//                println("SecurityException writing file: ${file.absolutePath}. Check permissions. ${e.message}")
//                throw IOException("Permission denied when writing to ${file.absolutePath}", e)
//            } catch (e: IOException) {
//                println("IOException writing file: ${file.absolutePath}. ${e.message}")
//                throw e
//            }
        }
    }

    actual suspend fun exists(relativePath: String) = withContext(dispatchers) {
//        resolvePath(relativePath).exists()
        false
    }

    actual suspend fun read(relativePath: String) = withContext(dispatchers) {
        // TODO: should not allow relative path to go up
        val file = File("") // TODO
        try {
            if (!file.exists() || !file.isFile) {
                return@withContext null
            }
            file.readBytes()
        } catch (_: FileNotFoundException) {
            null
        } catch (e: SecurityException) {
            println("SecurityException reading file: ${file.absolutePath}. Check permissions. ${e.message}")
            null
        } catch (e: IOException) {
            println("IOException reading file: ${file.absolutePath}. ${e.message}")
            null
        }
    }

    actual suspend fun listFiles(opts: ListFileOpts) = withContext(dispatchers) {
        val baseDir =
            File(currentWritablePath + File.separator + opts.fileType.getDirName())
        if (!baseDir.exists() || !baseDir.isDirectory) {
            return@withContext emptyList()
        }

        val fileFilter = when (opts.fileType) {
            FileType.GgufModel -> { file: File ->
                file.isFile && file.extension.equals(
                    "gguf",
                    ignoreCase = true
                )
            }
        }

        baseDir.listFiles(fileFilter)
            ?.map {
                FileModel(
                    name = it.name,
                    absolutePath = it.absolutePath,
                )
            } // Return only the relative name
            ?: emptyList()
    }
}
