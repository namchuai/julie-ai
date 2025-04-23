package ai.julie.core.network

data class HttpConfig(
    val protocol: String,
    val host: String,
    val headers: Map<String, String> = emptyMap(),
)