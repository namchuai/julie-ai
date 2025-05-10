package ai.julie.core.audio

import ai.julie.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.Mixer
import javax.sound.sampled.TargetDataLine

actual fun createAudioRecorder(): AudioRecorder = DesktopAudioRecorder()

class DesktopAudioRecorder : AudioRecorder {
    private val _recordingState = MutableStateFlow<AudioRecordingState>(AudioRecordingState.Idle)
    override val recordingState: Flow<AudioRecordingState> = _recordingState.asStateFlow()

    private var targetDataLine: TargetDataLine? = null
    private var audioFormat: AudioFormat? = null
    private var audioConfig: AudioConfig? = null
    private var recordingThread: Thread? = null
    private var audioBuffer: ByteArrayOutputStream? = null
    private var recordingStartTime: Long = 0

    override suspend fun startRecording(config: AudioConfig): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (isRecording()) {
                    return@withContext Result.failure(IllegalStateException("Already recording"))
                }

                audioConfig = config

                // Create audio format for recording - use a more compatible format
                audioFormat = AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    config.sampleRate.toFloat(),
                    config.bitsPerSample,
                    config.channels,
                    config.channels * config.bitsPerSample / 8, // frame size
                    config.sampleRate.toFloat(),
                    false // little endian
                )

                // List available audio input devices
                listAvailableAudioInputDevices()

                // Get audio line
                val info = DataLine.Info(TargetDataLine::class.java, audioFormat)

                Logger.d("Checking audio line support for format: $audioFormat")

                if (!AudioSystem.isLineSupported(info)) {
                    // Try alternative formats
                    val alternativeFormats = listOf(
                        AudioFormat(
                            16000.0f,
                            16,
                            1,
                            true,
                            false
                        ), // 16kHz, 16-bit, mono, signed, little endian
                        AudioFormat(
                            44100.0f,
                            16,
                            1,
                            true,
                            false
                        ), // 44.1kHz, 16-bit, mono, signed, little endian
                        AudioFormat(
                            8000.0f,
                            16,
                            1,
                            true,
                            false
                        )   // 8kHz, 16-bit, mono, signed, little endian
                    )

                    var supportedFormat: AudioFormat? = null
                    for (altFormat in alternativeFormats) {
                        val altInfo = DataLine.Info(TargetDataLine::class.java, altFormat)
                        if (AudioSystem.isLineSupported(altInfo)) {
                            supportedFormat = altFormat
                            Logger.d("Using alternative format: $altFormat")
                            break
                        }
                    }

                    if (supportedFormat == null) {
                        _recordingState.value =
                            AudioRecordingState.Error("No supported audio format found")
                        return@withContext Result.failure(IllegalStateException("No supported audio format found"))
                    }

                    audioFormat = supportedFormat
                } else {
                    Logger.d("Audio line is supported for requested format")
                }

                val finalInfo = DataLine.Info(TargetDataLine::class.java, audioFormat)

                // Try to get the best audio input device (built-in microphone preferred over Bluetooth)
                targetDataLine = getBestAudioInputDevice(finalInfo)

                // Open with a specific buffer size
                val bufferSize =
                    (audioFormat!!.sampleRate * audioFormat!!.frameSize * 2).toInt() // 2 seconds buffer
                Logger.d("Opening audio line with buffer size: $bufferSize")
                targetDataLine?.open(audioFormat, bufferSize)

                // Initialize buffer
                audioBuffer = ByteArrayOutputStream()

                // Start recording
                Logger.d("Starting target line...")
                targetDataLine?.start()

                // On macOS, sometimes we need to prime the line by attempting to read
                // This can help activate the audio line
                try {
                    val primeBuffer = ByteArray(1024)
                    val bytesRead = targetDataLine?.read(primeBuffer, 0, primeBuffer.size) ?: 0
                    Logger.d("Primed line with $bytesRead bytes")
                } catch (e: Exception) {
                    Logger.d("Failed to prime line: ${e.message}")
                }

                // Wait a moment for the line to become active
                var attempts = 0
                while (targetDataLine?.isActive != true && attempts < 10) {
                    Thread.sleep(50)
                    attempts++
                    Logger.d("Waiting for line to become active, attempt $attempts, active: ${targetDataLine?.isActive}")
                }

                Logger.d("Target line started, is active: ${targetDataLine?.isActive}, attempts: $attempts")
                Logger.d("Target line info: isOpen=${targetDataLine?.isOpen}, isRunning=${targetDataLine?.isRunning}")

                // If line is still not active, try a different approach - just start recording anyway
                if (targetDataLine?.isActive != true) {
                    Logger.w("Audio line is not active, but attempting to record anyway")
                    // Don't fail here - sometimes recording still works even if isActive returns false
                }

                recordingStartTime = System.currentTimeMillis()

                // Start recording thread
                recordingThread = Thread {
                    recordAudio()
                }
                recordingThread?.start()

                Logger.d("Recording thread started")

                _recordingState.value = AudioRecordingState.Recording
                Logger.d("Started audio recording with config: $config")

                Result.success(Unit)
            } catch (e: Exception) {
//                Logger.e("Failed to start audio recording", e)
                _recordingState.value = AudioRecordingState.Error("Failed to start recording", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun stopRecording(): Result<AudioRecordingResult> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("Stopping recording, isRecording: ${isRecording()}")

                if (!isRecording()) {
                    return@withContext Result.failure(IllegalStateException("Not recording"))
                }

                // Stop recording
                targetDataLine?.stop()
                targetDataLine?.close()

                Logger.d("Target line stopped and closed")

                // Wait for recording thread to finish
                recordingThread?.join(1000) // Wait up to 1 second

                Logger.d("Recording thread joined")

                val recordingEndTime = System.currentTimeMillis()
                val durationMs = recordingEndTime - recordingStartTime

                // Get recorded audio data
                val audioData = audioBuffer?.toByteArray()
                audioBuffer?.close()

                Logger.d("Audio data size: ${audioData?.size ?: 0} bytes")

                if (audioData == null || audioData.isEmpty()) {
                    _recordingState.value = AudioRecordingState.Error("No audio data recorded")
                    Logger.w("No audio data recorded - buffer was empty")
                    return@withContext Result.failure(IllegalStateException("No audio data recorded"))
                }

                // Convert byte array to float array (whisper format)
                val floatArray = convertToFloatArray(audioData, audioConfig?.bitsPerSample ?: 16)

                // Save audio to file for debugging
                saveAudioForDebugging(audioData, audioConfig?.sampleRate ?: 16000)

                val result = AudioRecordingResult(
                    audioData = floatArray,
                    sampleRate = audioConfig?.sampleRate ?: 16000,
                    durationMs = durationMs
                )

                _recordingState.value = AudioRecordingState.Stopped
                Logger.d("Stopped audio recording. Duration: ${durationMs}ms, Samples: ${floatArray.size}")

                // Clean up
                cleanup()

                Result.success(result)
            } catch (e: Exception) {
//                Logger.e("Failed to stop audio recording", e)
                _recordingState.value = AudioRecordingState.Error("Failed to stop recording", e)
                cleanup()
                Result.failure(e)
            }
        }
    }

    override fun isRecording(): Boolean {
        return _recordingState.value is AudioRecordingState.Recording
    }

    override fun release() {
        if (isRecording()) {
            targetDataLine?.stop()
            targetDataLine?.close()
            recordingThread?.interrupt()
        }
        cleanup()
    }

    private fun recordAudio() {
        try {
            val buffer = ByteArray(1024)
            val targetLine = targetDataLine ?: return
            val audioBuffer = audioBuffer ?: return

            Logger.d("Recording thread started, line active: ${targetLine.isActive}, open: ${targetLine.isOpen}")
            var totalBytesRead = 0
            var consecutiveZeroReads = 0

            // Record for up to 30 seconds or until stopped
            val maxRecordingTime = 30000 // 30 seconds in milliseconds
            val startTime = System.currentTimeMillis()

            while (!Thread.currentThread().isInterrupted &&
                (System.currentTimeMillis() - startTime) < maxRecordingTime
            ) {

                try {
                    // Try to read even if line doesn't report as active
                    val bytesRead = targetLine.read(buffer, 0, buffer.size)
                    if (bytesRead > 0) {
                        // Check if the buffer contains actual audio data or just zeros
                        val nonZeroBytes = buffer.take(bytesRead).count { it != 0.toByte() }

                        audioBuffer.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        consecutiveZeroReads = 0

                        // Log every 10000 bytes to show progress
                        if (totalBytesRead % 10000 == 0) {
                            Logger.d("Recording progress: $totalBytesRead bytes (nonZero in last chunk: $nonZeroBytes)")
                        }
                    } else {
                        consecutiveZeroReads++
                        // If we get too many zero reads, maybe the line isn't working
                        if (consecutiveZeroReads > 100) {
                            Logger.w("Too many zero reads, line may not be working")
                            break
                        }
                        // Small delay to prevent busy waiting
                        Thread.sleep(10)
                    }
                } catch (e: Exception) {
                    Logger.e("Error reading from audio line: ${e.message}")
                    break
                }
            }

            Logger.d("Recording thread finished, total bytes read: $totalBytesRead")
        } catch (e: Exception) {
            Logger.e("Error during audio recording: ${e.message}")
            _recordingState.value = AudioRecordingState.Error("Recording error", e)
        }
    }

    private fun convertToFloatArray(audioData: ByteArray, bitsPerSample: Int): FloatArray {
        return when (bitsPerSample) {
            16 -> {
                // Convert 16-bit PCM to float array
                val floats = FloatArray(audioData.size / 2)
                val buffer = ByteBuffer.wrap(audioData).order(ByteOrder.LITTLE_ENDIAN)

                for (i in floats.indices) {
                    val sample = buffer.getShort(i * 2)
                    floats[i] = sample / 32768.0f // Normalize to [-1.0, 1.0]
                }

                // Debug the conversion
                val nonZeroFloats = floats.count { it != 0.0f }
                val maxFloat = floats.maxOrNull() ?: 0.0f
                val minFloat = floats.minOrNull() ?: 0.0f
                val avgAbsFloat = floats.map { kotlin.math.abs(it) }.average()

                Logger.d("Float conversion: ${floats.size} samples, nonZero=$nonZeroFloats, max=$maxFloat, min=$minFloat, avgAbs=$avgAbsFloat")

                floats
            }

            else -> {
                Logger.w("Unsupported bits per sample: $bitsPerSample. Using 16-bit conversion.")
                convertToFloatArray(audioData, 16)
            }
        }
    }

    private fun getBestAudioInputDevice(info: DataLine.Info): TargetDataLine {
        val mixerInfos = AudioSystem.getMixerInfo()

        // Priority order: C922 Webcam > MacBook Pro Microphone > WH-1000XM5 > Built-in microphone > USB > Others > Bluetooth
        val priorityOrder = listOf(
            "c922", "webcam", // Your USB webcam microphone - try this first
            "macbook pro microphone", // Built-in microphone second
            "wh-1000xm5", // Your specific headphones
            "built-in", "internal", "microphone", "mic",
            "usb", "external",
            "bluetooth", "bt", "airpods", "headset"
        )

        var bestMixer: Mixer? = null
        var bestPriority = Int.MAX_VALUE

        for (mixerInfo in mixerInfos) {
            val mixer = AudioSystem.getMixer(mixerInfo)

            // Check if this mixer supports our target line
            if (mixer.isLineSupported(info)) {
                val deviceName = mixerInfo.name.lowercase()
                val description = mixerInfo.description.lowercase()

                // Find the priority of this device
                var priority = priorityOrder.size // Default to lowest priority
                for ((index, keyword) in priorityOrder.withIndex()) {
                    if (deviceName.contains(keyword) || description.contains(keyword)) {
                        priority = index
                        break
                    }
                }

                if (priority < bestPriority) {
                    bestPriority = priority
                    bestMixer = mixer
                    Logger.d("Found better audio input device: ${mixerInfo.name} (priority: $priority)")
                }
            }
        }

        return if (bestMixer != null) {
            val mixerInfo = bestMixer.mixerInfo
            Logger.d("Using selected audio input device: ${mixerInfo.name}")
            Logger.d("Device details: ${mixerInfo.description}, Vendor: ${mixerInfo.vendor}")
            bestMixer.getLine(info) as TargetDataLine
        } else {
            Logger.d("Using default audio input device")
            AudioSystem.getLine(info) as TargetDataLine
        }
    }

    private fun testMultipleFormats(mixer: Mixer): List<AudioFormat> {
        val testFormats = listOf(
            AudioFormat(16000.0f, 16, 1, true, false), // 16kHz mono
            AudioFormat(44100.0f, 16, 1, true, false), // 44.1kHz mono
            AudioFormat(48000.0f, 16, 1, true, false), // 48kHz mono
            AudioFormat(22050.0f, 16, 1, true, false), // 22.05kHz mono
            AudioFormat(16000.0f, 16, 2, true, false), // 16kHz stereo
            AudioFormat(44100.0f, 16, 2, true, false), // 44.1kHz stereo
            AudioFormat(48000.0f, 16, 2, true, false), // 48kHz stereo
        )

        val supportedFormats = mutableListOf<AudioFormat>()

        for (format in testFormats) {
            try {
                val info = DataLine.Info(TargetDataLine::class.java, format)
                if (mixer.isLineSupported(info)) {
                    supportedFormats.add(format)
                }
            } catch (e: Exception) {
                // Ignore format test failures
            }
        }

        return supportedFormats
    }

    private fun listAvailableAudioInputDevices() {
        try {
            Logger.d("=== Available Audio Input Devices ===")
            val mixerInfos = AudioSystem.getMixerInfo()

            for ((index, mixerInfo) in mixerInfos.withIndex()) {
                val mixer = AudioSystem.getMixer(mixerInfo)
                val targetLineInfos = mixer.getTargetLineInfo()

                if (targetLineInfos.isNotEmpty()) {
                    Logger.d("[$index] ${mixerInfo.name} - ${mixerInfo.description}")
                    Logger.d("    Vendor: ${mixerInfo.vendor}")
                    Logger.d("    Version: ${mixerInfo.version}")

                    // Check what formats this mixer supports
                    val supportedFormats = testMultipleFormats(mixer)
                    if (supportedFormats.isNotEmpty()) {
                        Logger.d("    ✓ Supported formats:")
                        supportedFormats.forEach { format ->
                            Logger.d("      - ${format.sampleRate}Hz, ${format.sampleSizeInBits}-bit, ${if (format.channels == 1) "mono" else "stereo"}")
                        }
                    } else {
                        Logger.d("    ✗ No supported recording formats found")
                    }
                }
            }
            Logger.d("=== End Audio Device List ===")
        } catch (e: Exception) {
            Logger.e("Error listing audio devices: ${e.message}")
        }
    }

    private fun saveAudioForDebugging(audioData: ByteArray, sampleRate: Int) {
        try {
            val debugDir = java.io.File(System.getProperty("user.home"), ".julie/debug")
            debugDir.mkdirs()

            val timestamp = System.currentTimeMillis()
            val fileName = "audio_${timestamp}.wav"
            val audioFile = java.io.File(debugDir, fileName)

            // Create WAV file
            val audioFormat = AudioFormat(
                sampleRate.toFloat(),
                16, // 16-bit
                1,  // mono
                true, // signed
                false // little endian
            )

            val audioInputStream = AudioInputStream(
                java.io.ByteArrayInputStream(audioData),
                audioFormat,
                audioData.size / audioFormat.frameSize.toLong()
            )

            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioFile)

            Logger.d("Audio saved to: ${audioFile.absolutePath}")
        } catch (e: Exception) {
            Logger.e("Failed to save audio for debugging: ${e.message}")
        }
    }

    private fun cleanup() {
        targetDataLine?.close()
        targetDataLine = null
        audioFormat = null
        audioConfig = null
        recordingThread = null
        audioBuffer?.close()
        audioBuffer = null
        _recordingState.value = AudioRecordingState.Idle
    }
}