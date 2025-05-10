package ai.julie.whisperbinding

actual class WhisperContext(val pointer: Long)

class AndroidWhisperBinding : WhisperBinding {
    companion object {
        init {
            System.loadLibrary("whisper_jni")
        }
    }

    override fun initContext(modelPath: String): WhisperContext? {
        val pointer = NativeMethods.initContext(modelPath)
        return if (pointer != 0L) WhisperContext(pointer) else null
    }

    override fun transcribe(context: WhisperContext, audioData: FloatArray): String {
        return NativeMethods.transcribe(context.pointer, audioData, audioData.size)
    }

    override fun freeContext(context: WhisperContext) {
        NativeMethods.freeContext(context.pointer)
    }

    override fun getVersion(): String {
        return NativeMethods.getWhisperVersion()
    }
}

private object NativeMethods {
    external fun initContext(modelPath: String): Long
    external fun freeContext(context: Long)
    external fun transcribe(context: Long, samples: FloatArray, nSamples: Int): String
    external fun getWhisperVersion(): String
}

actual fun createWhisperBinding(): WhisperBinding = AndroidWhisperBinding()