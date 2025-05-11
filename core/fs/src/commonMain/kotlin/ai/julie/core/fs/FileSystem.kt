package ai.julie.core.fs

import ai.julie.core.fs.model.FileModel
import ai.julie.core.fs.model.ListFileOpts

/**
 * Provides platform-specific file system operations.
 * Instances are typically created specific to the platform (e.g., passing Context on Android).
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class FileSystem {
    /**
     * Gets the primary path where the application can write files.
     * This might be configurable on some platforms (like desktop) but fixed on others (mobile).
     */
    fun getWritablePath(): String

    /**
     * Sets the writable path.
     * Note: This might only be applicable/functional on certain platforms (e.g., Desktop).
     * Throws an exception or returns false if not supported or unsuccessful.
     */
    fun setWritablePath(path: String): Boolean

    /**
     * Writes binary data to the specified path relative to the writable path.
     * Creates directories if they don't exist.
     * Overwrites the file if it already exists.
     */
    suspend fun write(relativePath: String, data: ByteArray)

    /**
     * Checks if a file or directory exists at the specified path relative to the writable path.
     */
    suspend fun exists(relativePath: String): Boolean

    /**
     * Reads the entire content of a file as binary data.
     * Returns null if the file doesn't exist or cannot be read.
     */
    suspend fun read(relativePath: String): ByteArray?

    /**
     * Lists files within the writable path, filtered by the provided options.
     * Returns a list of relative file paths.
     */
    suspend fun listFiles(opts: ListFileOpts): List<FileModel>
}
