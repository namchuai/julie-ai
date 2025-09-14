package ai.julie.core.eventbus

sealed interface Event

data class ErrorEvent(
    val title: String,
    val message: String,
    val type: ErrorType = ErrorType.General
) : Event

enum class ErrorType {
    General,
    ModelNotFound
}