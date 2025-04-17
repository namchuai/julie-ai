package ai.julie.storage

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun createDbDriverFactory(): DatabaseDriverFactory {
    // Helper object to get Koin context
    object : KoinComponent {
        val context: android.content.Context by inject()
    }.apply {
        return AndroidDatabaseDriverFactory(context)
    }
    // This part should technically not be reached, but needed for compilation
    error("Failed to get Context from Koin")
} 