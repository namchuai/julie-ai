package ai.julie.core.model.chatcompletion.create

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Prediction(
    @SerialName("content") val content: PredictionContentRequest,

    /**
     * The type of the predicted content you want to provide. This type is currently always content.
     */
    @SerialName("type") val type: String = "content"
)
