package ai.julie.logging

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File

actual object LoggingConfig {
    
    private lateinit var logFile: File
    
    private fun getAppDataDirectory(): String {
        val userHome = System.getProperty("user.home") ?: System.getenv("HOME") ?: "/tmp"
        val osName = System.getProperty("os.name")?.lowercase() ?: "unknown"
        
        return when {
            osName.contains("mac") -> "$userHome/Library/Application Support/Julie"
            osName.contains("windows") -> "${System.getenv("APPDATA") ?: "$userHome/AppData/Roaming"}/Julie"
            else -> "$userHome/.julie"
        }
    }
    
    actual fun initializeLogging() {
        val logDir = File(getAppDataDirectory(), "logs")
        logDir.mkdirs()
        
        // Create daily log files with timestamp using kotlinx.datetime
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dateString = "${today.year}-${today.monthNumber.toString().padStart(2, '0')}-${today.dayOfMonth.toString().padStart(2, '0')}"
        logFile = File(logDir, "julie-$dateString.log")
        
        // Initialize file logging through the Logger wrapper
        Logger.initializeFileLogging(logFile.absolutePath)
        
        // Log the initialization
        Logger.i("Logging initialized - console and file output enabled")
        Logger.i("Log file: ${logFile.absolutePath}")
    }
    
    actual fun getLogFilePath(): String {
        return if (::logFile.isInitialized) logFile.absolutePath else ""
    }
}

// Desktop implementation of FileLogger
actual class FileLogger private constructor(private val logFile: File) {
    
    actual companion object {
        actual fun create(logFilePath: String): FileLogger? {
            return try {
                val file = File(logFilePath)
                file.parentFile?.mkdirs()
                FileLogger(file)
            } catch (e: Exception) {
                println("Failed to create FileLogger: ${e.message}")
                null
            }
        }
    }
    
    actual fun log(level: String, message: String) {
        try {
            val now = Clock.System.now()
            val timestamp = now.toString() // ISO format by default
            val logEntry = "[$timestamp] [$level] $message\n"
            logFile.appendText(logEntry)
        } catch (e: Exception) {
            // Fallback to console if file writing fails
            println("Failed to write to log file: ${e.message}")
            println("[$level] $message")
        }
    }
}