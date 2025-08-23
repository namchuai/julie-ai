package ai.julie.feature.appsetting.domain

fun interface UpdateAppSetting {
    suspend fun updateAppSetting(setting: AppSetting)
}