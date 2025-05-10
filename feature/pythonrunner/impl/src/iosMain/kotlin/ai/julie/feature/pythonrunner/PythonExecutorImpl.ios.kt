package ai.julie.feature.pythonrunner

import ai.julie.feature.pythonrunner.domain.ExecuteCode
import ai.julie.feature.pythonrunner.domain.ExecuteCodeAndGetString
import ai.julie.feature.pythonrunner.domain.GetInstalledPackages
import ai.julie.feature.pythonrunner.domain.InstallPackage

actual class PythonExecutor actual constructor() :
    GetInstalledPackages,
    ExecuteCode,
    ExecuteCodeAndGetString,
    InstallPackage {

    actual override suspend fun getInstalledPackages(): List<String> {
        TODO("Not yet implemented")
    }

    actual override suspend fun execute(code: String): Any? {
        TODO("Not yet implemented")
    }

    actual override suspend fun executeAndGetString(code: String): String {
        TODO("Not yet implemented")
    }

    actual override suspend fun installPackage(packageName: String): String {
        TODO("Not yet implemented")
    }
}
