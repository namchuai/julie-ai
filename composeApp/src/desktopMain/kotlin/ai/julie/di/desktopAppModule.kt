package ai.julie.di

import ai.julie.ui.bottomstatusbar.BottomStatusBarViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val desktopAppModule = module {
    includes(appModule)

    // Desktop UI ViewModels
    viewModelOf(::BottomStatusBarViewModel)
}