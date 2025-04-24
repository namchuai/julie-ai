package ai.julie.core.model.configuration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteModelConfiguration(
    @SerialName("id") val id: String,

    @SerialName("provider") val provider: String,

    @SerialName("description") val description: String,

    @SerialName("protocol") val protocol: String,

    @SerialName("base_url") val baseUrl: String,
)
