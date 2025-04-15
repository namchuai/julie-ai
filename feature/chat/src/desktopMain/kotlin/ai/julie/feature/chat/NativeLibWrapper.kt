package ai.julie.feature.chat

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object NativeLibWrapper {
    init {
        try {
            // Load the library using its base name ("native").
            // The JVM will search paths defined in java.library.path.
            System.loadLibrary("native")
            println("Successfully loaded native library 'native'")
        } catch (e: UnsatisfiedLinkError) {
            println("Failed to load native library 'native': ${e.message}")
            // Provide more helpful error message, including the path it likely searched
            println("Check if the library exists and if java.library.path is set correctly:")
            println("java.library.path = ${System.getProperty("java.library.path")}")
            e.printStackTrace()
            throw e
        }
    }

    external actual fun strings(str: String): String?
    external actual fun getGlobalString(): String

    actual fun callNativeString(input: String): String {
        return strings(input) ?: "null was returned"
    }
}