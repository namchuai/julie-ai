package ai.julie.core.domain

import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration.Companion.seconds

class CreateChatCompletionStreamUseCase {

    operator fun invoke(
        messages: List<ChatMessage>,
    ): Flow<ChatCompletionChunk> {
        val openai = OpenAI(
            token = token = "placeholder",
            timeout = Timeout(socket = 60.seconds),
        )
        return openai.chatCompletions(
            ChatCompletionRequest(
                model = ModelId(id = "gpt-4.1"),
                messages = messages,
            )
        )
    }
}