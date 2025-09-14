package ai.julie.feature.promptlab.domain

fun interface DeletePromptTemplateContext {
    suspend fun deletePromptTemplateContext(id: String)
}