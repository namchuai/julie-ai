package ai.julie.feature.chat

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object NativeLibWrapper {
    fun strings(str: String): String?
    fun getGlobalString(): String
    fun callNativeString(input: String): String
}