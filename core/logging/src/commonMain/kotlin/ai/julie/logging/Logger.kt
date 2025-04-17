package ai.julie.logging

import co.touchlab.kermit.Logger

class Logger {

    companion object {
        fun v(message: String) {
            Logger.v { message }
        }

        fun v(message: () -> String) {
            Logger.v { message() }
        }

        fun e(message: String) {
            Logger.e { message }
        }

        fun e(message: () -> String) {
            Logger.e { message() }
        }

        fun w(message: String) {
            Logger.w { message }
        }

        fun w(message: () -> String) {
            Logger.w { message() }
        }

        fun i(message: String) {
            Logger.i { message }
        }

        fun i(message: () -> String) {
            Logger.i { message() }
        }

        fun d(message: String) {
            Logger.d { message }
        }

        fun d(message: () -> String) {
            Logger.d { message() }
        }
    }
}