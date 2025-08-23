package ai.julie.feature.jinjaparser.di

import ai.julie.feature.jinjaparser.data.JinjaTemplateServiceImpl
import ai.julie.feature.jinjaparser.domain.ProcessChatTemplate
import org.koin.dsl.module

val jinjaParserModule = module {
    single<ProcessChatTemplate> { JinjaTemplateServiceImpl() }
}