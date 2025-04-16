package ai.julie.feature.chat

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object NativeLibWrapper {
    external actual fun strings(str: String): String?
    external actual fun getGlobalString(): String

    actual fun callNativeString(input: String): String {
        return strings(input) ?: "null was returned"
    }
}