package ai.julie.core.fs.di

import ai.julie.core.fs.FileSystem
import org.koin.core.module.Module

/**
 * Actual implementation for Android to bind FileSystem.
 * It expects an Android Context to be provided in the Koin graph.
 */
internal actual fun Module.bindFileSystem() {
    // Use factory for FileSystem as it depends on Context
    // Koin's get() will resolve the Context provided by the application module.
    factory { FileSystem(get()) }
} 