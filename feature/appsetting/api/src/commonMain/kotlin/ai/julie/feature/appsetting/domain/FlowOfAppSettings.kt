package ai.julie.feature.appsetting.domain

import kotlinx.coroutines.flow.Flow

fun interface FlowOfAppSetting {
    fun flowOfAppSetting(): Flow<AppSetting>
}