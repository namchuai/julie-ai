package ai.julie.core.network.config

actual val openAiApiKey: String
    get() = System.getenv("OPENAI_API_KEY")
        ?: error("Environment variable \"OPENAI_API_KEY\" is not set.")
