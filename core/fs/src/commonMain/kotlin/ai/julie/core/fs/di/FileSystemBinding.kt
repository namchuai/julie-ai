package ai.julie.core.fs.di

import org.koin.core.module.Module

/**
 * Expect function to bind the platform-specific FileSystem implementation.
 * Actual implementations in platform source sets will provide the concrete binding.
 */
internal expect fun Module.bindFileSystem() 