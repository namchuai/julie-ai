package ai.julie.core.permission

import ai.julie.logging.Logger

actual fun createPermissionManager(): PermissionManager = AndroidPermissionManager()

class AndroidPermissionManager : PermissionManager {

    override suspend fun checkPermission(permission: Permission): PermissionStatus {
        // TODO: Implement Android permission checking using ContextCompat.checkSelfPermission
        Logger.w("Android permission checking not implemented yet")
        return PermissionStatus.NotDetermined
    }

    override suspend fun requestPermission(permission: Permission): PermissionStatus {
        // TODO: Implement Android permission requesting using ActivityCompat.requestPermissions
        Logger.w("Android permission requesting not implemented yet")
        return PermissionStatus.NotDetermined
    }

    override suspend fun openSettings() {
        // TODO: Implement opening Android app settings
        Logger.w("Android settings opening not implemented yet")
    }
}