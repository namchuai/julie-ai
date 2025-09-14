package ai.julie.di

import ai.julie.core.eventbus.di.eventBusModule
import ai.julie.feature.appsetting.di.appSettingModule
import ai.julie.feature.chat.di.chatModule
import ai.julie.feature.hardwaremonitor.di.hardwareMonitorModule
import ai.julie.feature.jinjaparser.di.jinjaParserModule
import ai.julie.feature.message.di.messageModule
import ai.julie.feature.modelconfig.di.modelConfigModule
import ai.julie.feature.modelmanagement.di.modelManagementModule
import ai.julie.feature.modelmarket.di.modelMarketModule
import ai.julie.feature.promptlab.di.promptLabModule
import ai.julie.feature.pythonrunner.di.pythonRunnerModule
import ai.julie.feature.thread.di.threadModule
import ai.julie.feature.toolmanagement.di.toolManagementModule
import ai.julie.panel.chat.ChatPanelViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    includes(eventBusModule)
    includes(appSettingModule)
    includes(chatModule)
    includes(threadModule)
    includes(messageModule)
    includes(jinjaParserModule)
    includes(modelConfigModule)
    includes(modelMarketModule)
    includes(modelManagementModule)
    includes(promptLabModule)
    includes(toolManagementModule)
    includes(pythonRunnerModule)
    includes(hardwareMonitorModule)

    viewModelOf(::ChatPanelViewModel)
}