package ai.julie.feature.message.di

import ai.julie.feature.message.data.MessageRepository
import ai.julie.feature.message.domain.CreateMessage
import ai.julie.feature.message.domain.DeleteMessage
import ai.julie.feature.message.domain.FlowOfMessages
import ai.julie.feature.message.domain.UpdateMessage
import org.koin.dsl.module

private const val DB_NAME = "julie-db"

val messageModule = module {

    // Message repository implementation - create one instance and bind to all interfaces
    single { MessageRepository.create(dbName = DB_NAME) }
    single<CreateMessage> { get<MessageRepository>() }
    single<FlowOfMessages> { get<MessageRepository>() }
    single<DeleteMessage> { get<MessageRepository>() }
    single<UpdateMessage> { get<MessageRepository>() }
}