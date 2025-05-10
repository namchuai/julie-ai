package ai.julie.core.domain

import ai.julie.core.data.permission.PermissionRepository
import ai.julie.core.permission.Permission
import ai.julie.core.permission.PermissionStatus
import ai.julie.logging.Logger

class RequestMicrophonePermissionUseCase(
    private val permissionRepository: PermissionRepository
) {
    /**
     * Request microphone permission from the user
     * @return Result indicating whether permission was granted
     */
    suspend fun execute(): Result<Boolean> {
        return try {
            Logger.d("Requesting microphone permission")

            // First check current status
            val currentStatus = permissionRepository.checkPermission(Permission.Microphone)

            when (currentStatus) {
                PermissionStatus.Granted -> {
                    Logger.d("Microphone permission already granted")
                    Result.success(true)
                }

                PermissionStatus.Denied -> {
                    Logger.w("Microphone permission was denied")
                    Result.success(false)
                }

                PermissionStatus.Restricted -> {
                    Logger.w("Microphone permission is restricted")
                    Result.success(false)
                }

                PermissionStatus.NotDetermined -> {
                    // Request permission
                    Logger.d("Requesting microphone permission from user")
                    val result = permissionRepository.requestPermission(Permission.Microphone)

                    when (result) {
                        PermissionStatus.Granted -> {
                            Logger.d("Microphone permission granted by user")
                            Result.success(true)
                        }

                        else -> {
                            Logger.w("Microphone permission denied by user")
                            Result.success(false)
                        }
                    }
                }
            }
        } catch (e: Exception) {
//            Logger.e("Error requesting microphone permission", e)
            Result.failure(e)
        }
    }

    /**
     * Check if microphone permission is currently granted
     * @return true if permission is granted, false otherwise
     */
    suspend fun hasPermission(): Boolean {
        return try {
            permissionRepository.hasPermission(Permission.Microphone)
        } catch (e: Exception) {
//            Logger.e("Error checking microphone permission", e)
            false
        }
    }

    /**
     * Open system settings for the user to manually grant permission
     */
    suspend fun openSettings() {
        try {
            Logger.d("Opening system settings for microphone permission")
            permissionRepository.openSettings()
        } catch (e: Exception) {
//            Logger.e("Error opening system settings", e)
        }
    }
}