package ai.julie.logging

expect object LoggingConfig {
    fun initializeLogging()
    fun getLogFilePath(): String
}