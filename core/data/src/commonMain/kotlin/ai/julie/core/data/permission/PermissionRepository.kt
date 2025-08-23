package ai.julie.core.data.permission

import ai.julie.core.permission.Permission
import ai.julie.core.permission.PermissionManager
import ai.julie.core.permission.PermissionStatus
import ai.julie.core.permission.createPermissionManager

interface PermissionRepository {
    suspend fun checkPermission(permission: Permission): PermissionStatus
    suspend fun requestPermission(permission: Permission): PermissionStatus
    suspend fun hasPermission(permission: Permission): Boolean
    suspend fun openSettings()
}

class PermissionRepositoryImpl(
    private val permissionManager: PermissionManager = createPermissionManager()
) : PermissionRepository {

    override suspend fun checkPermission(permission: Permission): PermissionStatus {
        return permissionManager.checkPermission(permission)
    }

    override suspend fun requestPermission(permission: Permission): PermissionStatus {
        return permissionManager.requestPermission(permission)
    }

    override suspend fun hasPermission(permission: Permission): Boolean {
        return permissionManager.hasPermission(permission)
    }

    override suspend fun openSettings() {
        permissionManager.openSettings()
    }
}