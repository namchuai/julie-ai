package ai.julie.feature.modelconfig.di

import ai.julie.feature.modelconfig.data.ModelContextRepository
import ai.julie.feature.modelconfig.data.ModelLoadRepository
import ai.julie.feature.modelconfig.data.ModelMetadataRepository
import ai.julie.feature.modelconfig.data.SamplingPresetRepository
import ai.julie.feature.modelconfig.domain.DeleteModelContextParam
import ai.julie.feature.modelconfig.domain.DeleteModelLoadParam
import ai.julie.feature.modelconfig.domain.DeleteModelMetadata
import ai.julie.feature.modelconfig.domain.FlowOfModelContextParam
import ai.julie.feature.modelconfig.domain.FlowOfModelLoadParam
import ai.julie.feature.modelconfig.domain.FlowOfModelMetadata
import ai.julie.feature.modelconfig.domain.FlowOfSamplingPresets
import ai.julie.feature.modelconfig.domain.RecreateModelContextUseCase
import ai.julie.feature.modelconfig.domain.RecreateModelContextUseCaseImpl
import ai.julie.feature.modelconfig.domain.StoreModelContextParam
import ai.julie.feature.modelconfig.domain.StoreModelLoadParam
import ai.julie.feature.modelconfig.domain.StoreModelMetadata
import ai.julie.feature.modelconfig.domain.ggufreader.GgufReader
import ai.julie.feature.modelconfig.domain.ggufreader.ReadGgufMetadata
import org.koin.dsl.bind
import org.koin.dsl.module

private const val DB_NAME = "julie-db"

val modelConfigModule = module {

    single<SamplingPresetRepository> { SamplingPresetRepository() }
    single<FlowOfSamplingPresets> { get<SamplingPresetRepository>() }

    single<ModelContextRepository> { ModelContextRepository.create(DB_NAME) }
    single<StoreModelContextParam> { get<ModelContextRepository>() }
    single<DeleteModelContextParam> { get<ModelContextRepository>() }
    single<FlowOfModelContextParam> { get<ModelContextRepository>() }

    single<ModelLoadRepository> { ModelLoadRepository.create(DB_NAME) }
    single<StoreModelLoadParam> { get<ModelLoadRepository>() }
    single<DeleteModelLoadParam> { get<ModelLoadRepository>() }
    single<FlowOfModelLoadParam> { get<ModelLoadRepository>() }

    single<ModelMetadataRepository> { ModelMetadataRepository.create(dbName = DB_NAME) }
    single<StoreModelMetadata> { get<ModelMetadataRepository>() }
    single<FlowOfModelMetadata> { get<ModelMetadataRepository>() }
    single<DeleteModelMetadata> { get<ModelMetadataRepository>() }

    // GGUF Reader
    factory { GgufReader() } bind ReadGgufMetadata::class

    // Context Recreation Use Case
    single<RecreateModelContextUseCase> { RecreateModelContextUseCaseImpl(get()) }

}