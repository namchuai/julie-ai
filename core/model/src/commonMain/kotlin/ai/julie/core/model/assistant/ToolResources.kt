package ai.julie.core.model.assistant

/**
 * A set of resources that are used by the assistant's tools.
 */
data class ToolResources(
    /**
     * Resources for the code interpreter tool.
     */
    val codeInterpreter: CodeInterpreterResources? = null,

    /**
     * Resources for the file search tool.
     */
    val fileSearch: FileSearchResources? = null
)

/**
 * Resources for the code interpreter tool.
 */
data class CodeInterpreterResources(
    /**
     * A list of file IDs made available to the `code_interpreter` tool.
     * There can be a maximum of 20 files associated with the tool.
     */
    val fileIds: List<String> = emptyList()
)

/**
 * Resources for the file search tool.
 */
data class FileSearchResources(
    /**
     * The ID of the vector store attached to this assistant.
     * There can be a maximum of 1 vector store attached to the assistant.
     */
    val vectorStoreIds: List<String> = emptyList()
)
