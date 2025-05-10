package ai.julie.core.permission

/**
 * Represents the status of a permission request
 */
sealed class PermissionStatus {
    object Granted : PermissionStatus()
    object Denied : PermissionStatus()
    object NotDetermined : PermissionStatus()
    object Restricted : PermissionStatus()
}

/**
 * Represents different types of permissions
 */
sealed class Permission {
    object Microphone : Permission()
    object Camera : Permission()
    object Location : Permission()
    // Add more permissions as needed
}

/**
 * Cross-platform permission manager interface
 */
interface PermissionManager {
    /**
     * Check the current status of a permission
     * @param permission The permission to check
     * @return Current permission status
     */
    suspend fun checkPermission(permission: Permission): PermissionStatus

    /**
     * Request a permission from the user
     * @param permission The permission to request
     * @return The resulting permission status after user interaction
     */
    suspend fun requestPermission(permission: Permission): PermissionStatus

    /**
     * Check if a permission has been granted
     * @param permission The permission to check
     * @return true if granted, false otherwise
     */
    suspend fun hasPermission(permission: Permission): Boolean {
        return checkPermission(permission) == PermissionStatus.Granted
    }

    /**
     * Open the system settings for the app (useful when permission is permanently denied)
     */
    suspend fun openSettings()
}

/**
 * Factory function to create platform-specific PermissionManager
 */
expect fun createPermissionManager(): PermissionManager