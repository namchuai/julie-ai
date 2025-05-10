package ai.julie.core.permission

import ai.julie.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.net.URI

actual fun createPermissionManager(): PermissionManager = DesktopPermissionManager()

class DesktopPermissionManager : PermissionManager {

    override suspend fun checkPermission(permission: Permission): PermissionStatus {
        return withContext(Dispatchers.IO) {
            when (permission) {
                is Permission.Microphone -> checkMicrophonePermission()
                is Permission.Camera -> checkCameraPermission()
                is Permission.Location -> checkLocationPermission()
            }
        }
    }

    override suspend fun requestPermission(permission: Permission): PermissionStatus {
        return withContext(Dispatchers.IO) {
            when (permission) {
                is Permission.Microphone -> requestMicrophonePermission()
                is Permission.Camera -> requestCameraPermission()
                is Permission.Location -> requestLocationPermission()
            }
        }
    }

    override suspend fun openSettings() {
        withContext(Dispatchers.IO) {
            try {
                // On macOS, open System Preferences -> Security & Privacy -> Privacy
                if (Desktop.isDesktopSupported()) {
                    val desktop = Desktop.getDesktop()
                    if (desktop.isSupported(Desktop.Action.OPEN)) {
                        // Open System Preferences
                        val uri =
                            URI("x-apple.systempreferences:com.apple.preference.security?Privacy")
                        desktop.browse(uri)
                    }
                }
            } catch (e: Exception) {
                Logger.e("Failed to open system settings: ${e.message}")
            }
        }
    }

    private fun checkMicrophonePermission(): PermissionStatus {
        return try {
            // On macOS, we can try to check if microphone access is available
            // This is a simplified check - in a real implementation, you might use
            // JNI to call macOS APIs like AVAudioSession.requestRecordPermission

            // For now, we'll assume it's not determined and will be requested when needed
            // This is because Java/Kotlin doesn't have direct access to macOS privacy APIs
            PermissionStatus.NotDetermined
        } catch (e: Exception) {
            Logger.e("Error checking microphone permission: ${e.message}")
            PermissionStatus.NotDetermined
        }
    }

    private fun requestMicrophonePermission(): PermissionStatus {
        return try {
            // On macOS, we need to actually try to access the microphone to trigger the permission dialog
            Logger.d("Requesting microphone permission from user")

            // Try to create and open a microphone line - this should trigger the macOS permission dialog
            val audioFormat = javax.sound.sampled.AudioFormat(
                javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED,
                16000.0f, // sample rate
                16,       // bits per sample
                1,        // channels (mono)
                2,        // frame size
                16000.0f, // frame rate
                false     // little endian
            )

            val info = javax.sound.sampled.DataLine.Info(
                javax.sound.sampled.TargetDataLine::class.java,
                audioFormat
            )

            if (!javax.sound.sampled.AudioSystem.isLineSupported(info)) {
                Logger.w("Microphone line not supported")
                return PermissionStatus.Denied
            }

            // This should trigger the macOS permission dialog
            val line =
                javax.sound.sampled.AudioSystem.getLine(info) as javax.sound.sampled.TargetDataLine
            line.open(audioFormat)

            // Actually try to start and read from the microphone - this is what triggers the real permission dialog
            line.start()

            // Try to read a small amount of data to verify permission
            val testBuffer = ByteArray(4096)
            var totalBytesRead = 0
            var nonZeroBytes = 0

            // Try to read for up to 2 seconds to get real audio data
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 2000) {
                val bytesRead = line.read(testBuffer, 0, testBuffer.size)
                if (bytesRead > 0) {
                    totalBytesRead += bytesRead
                    nonZeroBytes += testBuffer.take(bytesRead).count { it != 0.toByte() }
                }
                if (totalBytesRead > 8192) break // Got enough data
            }

            line.stop()
            line.close()

            Logger.d("Permission test: read $totalBytesRead bytes, nonZero: $nonZeroBytes")

            if (nonZeroBytes > 0) {
                Logger.d("Microphone permission granted - detected actual audio")
                return PermissionStatus.Granted
            } else if (totalBytesRead > 0) {
                Logger.w("Microphone permission unclear - reading zeros only")
                return PermissionStatus.Denied
            } else {
                Logger.w("Microphone permission denied - no data read")
                return PermissionStatus.Denied
            }

        } catch (e: javax.sound.sampled.LineUnavailableException) {
            Logger.w("Microphone permission denied by user")
            return PermissionStatus.Denied
        } catch (e: SecurityException) {
            Logger.w("Microphone permission denied by user")
            return PermissionStatus.Denied
        } catch (e: Exception) {
            Logger.e("Error requesting microphone permission: ${e.message}")
            return PermissionStatus.Denied
        }
    }

    private fun checkCameraPermission(): PermissionStatus {
        // Similar approach for camera
        return PermissionStatus.NotDetermined
    }

    private fun requestCameraPermission(): PermissionStatus {
        return PermissionStatus.NotDetermined
    }

    private fun checkLocationPermission(): PermissionStatus {
        return PermissionStatus.NotDetermined
    }

    private fun requestLocationPermission(): PermissionStatus {
        return PermissionStatus.NotDetermined
    }
}