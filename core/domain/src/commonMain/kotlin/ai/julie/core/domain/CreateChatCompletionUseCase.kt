package ai.julie.core.domain

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.StreamOptions
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.seconds

class CreateChatCompletionUseCase {

    suspend operator fun invoke(
        messages: List<ChatMessage>,
    ): ChatCompletion {
        val openai = OpenAI(
            token = "placeholder",
            timeout = Timeout(socket = 60.seconds),
        )
        return openai.chatCompletion(
            ChatCompletionRequest(
                model = ModelId(id = "gpt-4.1"),
                messages = messages,
                streamOptions = StreamOptions()
            )
        )
    }
}
