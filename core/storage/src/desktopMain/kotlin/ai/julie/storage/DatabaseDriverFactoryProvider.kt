package ai.julie.storage

// actual implementation for Desktop
actual fun createDbDriverFactory(): DatabaseDriverFactory = DesktopDatabaseDriverFactory() 