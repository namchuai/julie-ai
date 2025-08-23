package ai.julie.core.data.whisper

/**
 * Represents a whisper model with metadata
 */
data class WhisperModel(
    val id: String,
    val name: String,
    val description: String,
    val size: String,
    val downloadUrl: String,
    val filename: String,
    val isMultilingual: Boolean,
    val isQuantized: Boolean,
    val language: String? = null // for English-only models
) {
    companion object {
        val AVAILABLE_MODELS = listOf(
            WhisperModel(
                id = "tiny",
                name = "Tiny",
                description = "Smallest model, fast but less accurate",
                size = "39 MB",
                downloadUrl = "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.bin",
                filename = "ggml-tiny.bin",
                isMultilingual = true,
                isQuantized = false
            ),
            WhisperModel(
                id = "tiny.en",
                name = "Tiny (English)",
                description = "Smallest English-only model",
                size = "39 MB",
                downloadUrl = "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.en.bin",
                filename = "ggml-tiny.en.bin",
                isMultilingual = false,
                isQuantized = false,
                language = "en"
            ),
            WhisperModel(
                id = "base",
                name = "Base",
                description = "Good balance of speed and accuracy",
                size = "148 MB",
                downloadUrl = "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-base.bin",
                filename = "ggml-base.bin",
                isMultilingual = true,
                isQuantized = false
            ),
            WhisperModel(
                id = "base.en",
                name = "Base (English)",
                description = "Good balance for English-only",
                size = "148 MB",
                downloadUrl = "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-base.en.bin",
                filename = "ggml-base.en.bin",
                isMultilingual = false,
                isQuantized = false,
                language = "en"
            ),
            WhisperModel(
                id = "small",
                name = "Small",
                description = "Better accuracy than base, slower",
                size = "488 MB",
                downloadUrl = "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-small.bin",
                filename = "ggml-small.bin",
                isMultilingual = true,
                isQuantized = false
            ),
            WhisperModel(
                id = "small.en",
                name = "Small (English)",
                description = "Better accuracy for English-only",
                size = "488 MB",
                downloadUrl = "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-small.en.bin",
                filename = "ggml-small.en.bin",
                isMultilingual = false,
                isQuantized = false,
                language = "en"
            ),
            WhisperModel(
                id = "medium",
                name = "Medium",
                description = "Good accuracy, reasonable speed",
                size = "1.5 GB",
                downloadUrl = "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-medium.bin",
                filename = "ggml-medium.bin",
                isMultilingual = true,
                isQuantized = false
            ),
            WhisperModel(
                id = "medium.en",
                name = "Medium (English)",
                description = "Good accuracy for English-only",
                size = "1.5 GB",
                downloadUrl = "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-medium.en.bin",
                filename = "ggml-medium.en.bin",
                isMultilingual = false,
                isQuantized = false,
                language = "en"
            )
        )

        fun getById(id: String): WhisperModel? = AVAILABLE_MODELS.find { it.id == id }

        fun getRecommended(): WhisperModel = getById("base.en") ?: AVAILABLE_MODELS.first()
    }
}