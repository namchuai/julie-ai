package ai.julie.feature.appsetting.di

import ai.julie.feature.appsetting.data.AppSettingRepository
import ai.julie.feature.appsetting.data.MainWindowRepository
import ai.julie.feature.appsetting.domain.FlowOfAppSetting
import ai.julie.feature.appsetting.domain.GetMainWindowSetting
import ai.julie.feature.appsetting.domain.UpdateAppSetting
import ai.julie.feature.appsetting.domain.UpdateMainWindowSetting
import org.koin.dsl.module

private const val DB_NAME = "julie-db"

val appSettingModule = module {
    single<AppSettingRepository> {
        AppSettingRepository.create(dbName = DB_NAME)
    }
    
    single<FlowOfAppSetting> {
        get<AppSettingRepository>()
    }
    
    single<UpdateAppSetting> {
        get<AppSettingRepository>()
    }
    
    single<MainWindowRepository> {
        MainWindowRepository.create(dbName = DB_NAME)
    }
    
    single<UpdateMainWindowSetting> {
        get<MainWindowRepository>()
    }
    
    single<GetMainWindowSetting> {
        get<MainWindowRepository>()
    }
}