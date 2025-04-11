package ai.julie.core.model.message

/**
 * Represents a tool that can be used with file attachments.
 */
sealed class Tool {
    /**
     * Code interpreter tool.
     */
    data class CodeInterpreter(
        /**
         * The type of tool being defined: `code_interpreter`
         */
        val type: String = "code_interpreter"
    ) : Tool()

    /**
     * File search tool.
     */
    data class FileSearch(
        /**
         * The type of tool being defined: `file_search`
         */
        val type: String = "file_search"
    ) : Tool()
}
