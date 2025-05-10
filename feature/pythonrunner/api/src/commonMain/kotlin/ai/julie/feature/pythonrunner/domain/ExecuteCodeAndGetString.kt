package ai.julie.feature.pythonrunner.domain

fun interface ExecuteCodeAndGetString {
    suspend fun executeAndGetString(code: String): String
}
