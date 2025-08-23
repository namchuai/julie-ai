package ai.julie.feature.pythonrunner

import ai.julie.feature.pythonrunner.domain.ExecuteCode
import ai.julie.feature.pythonrunner.domain.ExecuteCodeAndGetString
import ai.julie.feature.pythonrunner.domain.GetInstalledPackages
import ai.julie.feature.pythonrunner.domain.InstallPackage

expect class PythonExecutor() :
    GetInstalledPackages,
    ExecuteCode,
    ExecuteCodeAndGetString,
    InstallPackage