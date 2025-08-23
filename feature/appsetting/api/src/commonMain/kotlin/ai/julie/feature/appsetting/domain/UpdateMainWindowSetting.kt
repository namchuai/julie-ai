package ai.julie.feature.appsetting.domain

fun interface UpdateMainWindowSetting {
    suspend fun updateMainWindowSetting(setting: MainWindowSetting)
}