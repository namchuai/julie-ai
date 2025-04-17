package ai.julie.storage

// actual implementation for iOS
actual fun createDbDriverFactory(): DatabaseDriverFactory = IOSDatabaseDriverFactory() 