package ai.julie.whisperbinding

import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.path

actual class WhisperContext(val pointer: Long)

class DesktopWhisperBinding : WhisperBinding {
    companion object {
        init {
            try {
                // Find the correct library path - try multiple possible locations
                val currentDir = System.getProperty("user.dir")
                val possiblePaths = listOf(
                    "$currentDir/core/whisperbinding/build/native-libs",
                    "$currentDir/../core/whisperbinding/build/native-libs",
                    "${currentDir.substringBeforeLast("/")}/core/whisperbinding/build/native-libs"
                )

                var libPath: String? = null
                for (path in possiblePaths) {
                    val libDir = PlatformFile(path)
                    if (libDir.exists()) {
                        libPath = path
                        break
                    }
                }

                if (libPath == null) {
                    println("✗ Library directory not found in any of these locations:")
                    possiblePaths.forEach { println("  - $it") }
                    println("Current working directory: $currentDir")
                    throw Exception("Library directory not found")
                }

                println("Loading native libraries from: $libPath")

                // Load dependencies first in the correct order using absolute paths
                val dependencies = listOf(
                    "libggml-base.dylib",
                    "libggml-blas.dylib",
                    "libggml-metal.dylib",
                    "libggml-cpu.dylib",
                    "libggml.dylib",
                    "libwhisper.dylib",
                    "libwhisper_jni.dylib"
                )

                for (libName in dependencies) {
                    val libFile = PlatformFile("$libPath/$libName")
                    if (libFile.exists()) {
                        try {
                            System.load(libFile.path)
                            println("✓ Loaded native library: $libName")
                        } catch (e: Exception) {
                            println("✗ Failed to load library $libName: ${e.message}")
                            if (libName == "libwhisper_jni.dylib") {
                                throw e
                            }
                        }
                    } else {
                        println("✗ Library file not found: ${libFile.path}")
                        if (libName == "libwhisper_jni.dylib") {
                            throw Exception("Required library not found: $libName")
                        }
                    }
                }

                println("✓ All native libraries loaded successfully")
            } catch (e: Exception) {
                println("✗ Error loading native libraries: ${e.message}")
                e.printStackTrace()
                throw e
            }
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
    @JvmStatic
    external fun initContext(modelPath: String): Long

    @JvmStatic
    external fun freeContext(context: Long)

    @JvmStatic
    external fun transcribe(context: Long, samples: FloatArray, nSamples: Int): String

    @JvmStatic
    external fun getWhisperVersion(): String
}

actual fun createWhisperBinding(): WhisperBinding = DesktopWhisperBinding()