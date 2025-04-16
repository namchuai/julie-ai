package ai.julie.core.model.createchatcompletion

/**
 * Represents the stop sequences for the API.
 * The API will stop generating further tokens when it encounters any of these sequences.
 * The returned text will not contain the stop sequence(s).
 */
sealed class StopOption {
    /**
     * Represents a single stop sequence.
     * @param value The stop sequence string.
     */
    data class Single(val value: String) : StopOption()

    /**
     * Represents multiple stop sequences (up to 4).
     * @param values A list of stop sequence strings.
     */
    data class Multiple(val values: List<String>) : StopOption()
} 