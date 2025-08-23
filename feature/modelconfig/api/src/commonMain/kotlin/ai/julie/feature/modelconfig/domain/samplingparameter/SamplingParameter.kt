package ai.julie.feature.modelconfig.domain.samplingparameter

sealed interface SamplingParameter<T> {
    val key: String

    val value: T
}

//enum class LlmSamplingParameter(val value: String) {
//    TEMPERATURE("temperature"),
//    TOP_P("top_p"),
//    TOP_K("top_k"),
//    MIN_P("min_p"),
//    TYPICAL_P("typical_p"),
//    REPEAT_PENALTY("repeat_penalty"),
//    FREQUENCY_PENALTY("frequency_penalty"),
//    PRESENCE_PENALTY("presence_penalty"),
//    CONTEXT_LENGTH("context_length"),
//    MIROSTAT("mirostat"),
//    MIROSTAT_TAU("mirostat_tau"),
//    MIROSTAT_ETA("mirostat_eta"),
//    TFS_Z("tfs_z"),
//}

sealed interface ControllerBehavior<T>

data class Slider<T>(
    val min: T,
    val max: T,
    val step: T,
) : ControllerBehavior<T>

data object NoController : ControllerBehavior<Nothing>