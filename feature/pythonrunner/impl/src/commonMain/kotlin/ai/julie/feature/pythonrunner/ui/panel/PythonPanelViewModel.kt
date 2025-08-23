package ai.julie.feature.pythonrunner.ui.panel

import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import ai.julie.feature.pythonrunner.domain.ExecuteCodeAndGetString
import ai.julie.feature.pythonrunner.domain.GetInstalledPackages
import ai.julie.feature.pythonrunner.domain.InstallPackage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PythonPanelViewModel(
    private val getInstalledPackages: GetInstalledPackages,
    private val executeCodeAndGetString: ExecuteCodeAndGetString,
    private val installPackage: InstallPackage,
) : ViewModel() {

    val state = viewModelState(
        savedStateBehaviour = doNotSaveState(),
        loadTimeReporter = doNotReportLoadTime(),
        initialState = PythonPanelState(command = "", packageName = "")
    )

    fun onCommandUpdate(command: String) {
        state.update {
            it.copy(command = command)
        }
    }

    fun onPackageNameUpdate(packageName: String) {
        state.update {
            it.copy(packageName = packageName)
        }
    }

    fun onExecuteCodeClick() {
        viewModelScope.launch {
            val currentCommand = state.value.command
            val result = executeCodeAndGetString.executeAndGetString(currentCommand)
            println("Python output: $result")
        }
    }

    fun onGetInstalledPackagesClick() {
        viewModelScope.launch {
            val result = getInstalledPackages.getInstalledPackages()
            println(result.first())
        }
    }

    fun onInstallPackageClick() {
        viewModelScope.launch {
            val packageName = state.value.packageName.trim()
            if (packageName.isNotEmpty()) {
                val result = installPackage.installPackage(packageName)
                println("Package installation result: $result")
            } else {
                println("Please enter a package name")
            }
        }
    }
}