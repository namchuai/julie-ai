package ai.julie.feature.promptlab.ui.prompttemplate

data class PromptTemplateUiState(
    val isLoading: Boolean = false,
    val name: String = "",
    val content: String = "",
    val variables: Map<String, String>,
    val previewValues: Map<String, String>,
    val showAddVariableDialog: Boolean = false,
    val error: String? = null,
    val savedSuccessfully: Boolean = false
) {
    val canSaveComputed: Boolean
        get() = name.isNotBlank() && content.isNotBlank()
}
