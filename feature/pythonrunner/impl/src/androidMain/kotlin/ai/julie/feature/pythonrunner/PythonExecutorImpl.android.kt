package ai.julie.feature.pythonrunner

import ai.julie.feature.pythonrunner.domain.ExecuteCode
import ai.julie.feature.pythonrunner.domain.ExecuteCodeAndGetString
import ai.julie.feature.pythonrunner.domain.GetInstalledPackages

actual class PythonExecutor actual constructor() :
    GetInstalledPackages,
    ExecuteCode,
    ExecuteCodeAndGetString {

    override suspend fun execute(code: String): Result<Any?> {
        throw IllegalStateException("Python execution not supported on Android")
    }

    override suspend fun executeAndGetString(code: String): Result<String> {
        throw IllegalStateException("Python execution not supported on Android")
    }

    actual override suspend fun getInstalledPackages(): List<String> {
        throw IllegalStateException("Python execution not supported on Android")
    }
}