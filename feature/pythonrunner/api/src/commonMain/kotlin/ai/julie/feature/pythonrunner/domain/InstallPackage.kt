package ai.julie.feature.pythonrunner.domain

fun interface InstallPackage {
    suspend fun installPackage(packageName: String): String
}
