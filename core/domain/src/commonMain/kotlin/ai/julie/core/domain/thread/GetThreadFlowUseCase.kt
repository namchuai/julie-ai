package ai.julie.core.domain.thread

import ai.julie.core.data.thread.ThreadRepository

class GetThreadFlowUseCase(
    private val threadRepository: ThreadRepository,
) {

    operator fun invoke() = threadRepository.threadFlow()
}