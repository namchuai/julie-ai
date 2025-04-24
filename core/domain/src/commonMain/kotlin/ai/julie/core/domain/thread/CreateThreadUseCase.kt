package ai.julie.core.domain.thread

import ai.julie.core.data.thread.ThreadRepository
import com.aallam.openai.api.assistant.ToolResources
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadId
import kotlinx.datetime.Clock
import kotlin.uuid.Uuid

class CreateThreadUseCase(
    private val threadRepository: ThreadRepository,
) {

    suspend operator fun invoke(
        toolResources: ToolResources? = null,
        metadata: Map<String, String> = emptyMap(),
    ) = threadRepository.storeThread(
        Thread(
            id = ThreadId(Uuid.random().toHexString()),
            objectType = "thread",
            createdAt = Clock.System.now().epochSeconds.toInt(),
            toolResources = toolResources,
            metadata = metadata,
        )
    )
}