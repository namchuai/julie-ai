package ai.julie.feature.promptlab.domain

fun interface DeletePromptTemplate {
    suspend fun deletePromptTemplate(id: String)
}