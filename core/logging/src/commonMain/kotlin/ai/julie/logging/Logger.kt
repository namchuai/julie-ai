package ai.julie.logging

import co.touchlab.kermit.Logger as KermitLogger

class Logger {

    companion object {
        private var fileWriter: FileLogger? = null
        
        fun initializeFileLogging(logFilePath: String) {
            fileWriter = FileLogger.create(logFilePath)
        }
        
        fun v(message: String) {
            KermitLogger.v { message }
            fileWriter?.log("V", message)
        }

        fun v(message: () -> String) {
            val msg = message()
            KermitLogger.v { msg }
            fileWriter?.log("V", msg)
        }

        fun e(message: String) {
            KermitLogger.e { message }
            fileWriter?.log("E", message)
        }

        fun e(message: () -> String) {
            val msg = message()
            KermitLogger.e { msg }
            fileWriter?.log("E", msg)
        }

        fun w(message: String) {
            KermitLogger.w { message }
            fileWriter?.log("W", message)
        }

        fun w(message: () -> String) {
            val msg = message()
            KermitLogger.w { msg }
            fileWriter?.log("W", msg)
        }

        fun i(message: String) {
            KermitLogger.i { message }
            fileWriter?.log("I", message)
        }

        fun i(message: () -> String) {
            val msg = message()
            KermitLogger.i { msg }
            fileWriter?.log("I", msg)
        }

        fun d(message: String) {
            KermitLogger.d { message }
            fileWriter?.log("D", message)
        }

        fun d(message: () -> String) {
            val msg = message()
            KermitLogger.d { msg }
            fileWriter?.log("D", msg)
        }
    }
}

// Interface for file logging
expect class FileLogger {
    companion object {
        fun create(logFilePath: String): FileLogger?
    }
    
    fun log(level: String, message: String)
}