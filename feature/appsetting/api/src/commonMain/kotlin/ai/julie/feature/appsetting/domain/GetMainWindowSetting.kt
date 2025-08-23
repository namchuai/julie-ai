package ai.julie.feature.appsetting.domain

fun interface GetMainWindowSetting {
    suspend fun getMainWindowSetting(): MainWindowSetting
}