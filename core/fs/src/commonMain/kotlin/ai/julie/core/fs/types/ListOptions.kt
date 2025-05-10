package ai.julie.core.fs.types

enum class FileType {
    GgufModel // Represents GGUF model files (usually .gguf extension)
    // Add other types as needed, e.g., JsonConfig, LogFile
}

/**
 * Returns the conventional subdirectory name associated with this file type.
 */
fun FileType.getDirName(): String = when (this) {
    FileType.GgufModel -> "models"

    else -> throw IllegalArgumentException("Unknown FileType: $this")
}

data class ListFileOpts(
    val fileType: FileType
    // Could add other options later, e.g., recursive: Boolean = false
) 