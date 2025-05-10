package ai.julie.feature.pythonrunner.domain

fun interface ExecuteCode {
    suspend fun execute(code: String): Any?
}
