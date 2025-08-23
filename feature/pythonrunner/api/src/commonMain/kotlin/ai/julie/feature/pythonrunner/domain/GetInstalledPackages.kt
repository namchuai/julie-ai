package ai.julie.feature.pythonrunner.domain

fun interface GetInstalledPackages {
    suspend fun getInstalledPackages(): List<String>
}
