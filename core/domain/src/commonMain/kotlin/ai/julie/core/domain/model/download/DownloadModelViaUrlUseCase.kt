package ai.julie.core.domain.model.download

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DownloadModelViaUrlUseCase() {
    suspend operator fun invoke(url: String)
}
