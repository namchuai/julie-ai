package ai.julie.feature.thread.di

import ai.julie.feature.thread.data.ThreadRepository
import ai.julie.feature.thread.domain.ClearActiveThread
import ai.julie.feature.thread.domain.CreateThread
import ai.julie.feature.thread.domain.DeleteThread
import ai.julie.feature.thread.domain.FlowOfActiveThread
import ai.julie.feature.thread.domain.FlowOfThread
import ai.julie.feature.thread.domain.FlowOfThreads
import ai.julie.feature.thread.domain.SetActiveThread
import ai.julie.feature.thread.domain.UpdateThreadSamplingPresetId
import ai.julie.feature.thread.domain.UpdateThreadTimestamp
import ai.julie.feature.thread.navigation.ThreadNavigation
import ai.julie.feature.thread.navigation.ThreadNavigationImpl
import ai.julie.feature.thread.ui.threadlisting.ThreadListingViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

private const val DB_NAME = "julie-db"

val threadModule = module {

    // Thread repository implementation - create one instance and bind to all interfaces
    single { ThreadRepository.create(dbName = DB_NAME) }
    single<CreateThread> { get<ThreadRepository>() }
    single<FlowOfThreads> { get<ThreadRepository>() }
    single<FlowOfThread> { get<ThreadRepository>() }
    single<DeleteThread> { get<ThreadRepository>() }
    single<SetActiveThread> { get<ThreadRepository>() }
    single<FlowOfActiveThread> { get<ThreadRepository>() }
    single<ClearActiveThread> { get<ThreadRepository>() }
    single<UpdateThreadTimestamp> { get<ThreadRepository>() }
    single<UpdateThreadSamplingPresetId> { get<ThreadRepository>() }

    // Navigation binding
    singleOf(::ThreadNavigationImpl) bind ThreadNavigation::class

    // ViewModels
    viewModelOf(::ThreadListingViewModel)
}