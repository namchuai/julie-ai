package ai.julie.core.domain.prompt

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory implementation of PromptTemplateRepository
 * Provides default templates and allows runtime template management
 */
class PromptTemplateRepositoryImpl : PromptTemplateRepository {

    private val _templates = MutableStateFlow(
        DefaultPrompts.getAllDefaultTemplates().associateBy { it.id }
    )

    private val templates = _templates.asStateFlow()

    override fun getAllTemplates(): Flow<List<PromptTemplateData>> {
        return templates.map { it.values.toList() }
    }

    override suspend fun getTemplate(id: String): PromptTemplateData? {
        return _templates.value[id]
    }

    override suspend fun getTemplatesByCategory(category: String): List<PromptTemplateData> {
        return _templates.value.values.filter { it.category == category }
    }

    override suspend fun getTemplatesByTag(tag: String): List<PromptTemplateData> {
        return _templates.value.values.filter { tag in it.tags }
    }

    override suspend fun saveTemplate(template: PromptTemplateData) {
        _templates.value = _templates.value + (template.id to template)
    }

    override suspend fun deleteTemplate(id: String) {
        _templates.value = _templates.value - id
    }

    override suspend fun searchTemplates(query: String): List<PromptTemplateData> {
        val lowercaseQuery = query.lowercase()
        return _templates.value.values.filter { template ->
            template.name.lowercase().contains(lowercaseQuery) ||
                    template.description?.lowercase()?.contains(lowercaseQuery) == true ||
                    template.tags.any { it.lowercase().contains(lowercaseQuery) }
        }
    }

    override suspend fun getDefaultTemplates(): List<PromptTemplateData> {
        return DefaultPrompts.getAllDefaultTemplates()
    }
}