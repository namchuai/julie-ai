package ai.julie.core.domain.prompt

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing prompt templates
 */
interface PromptTemplateRepository {

    /**
     * Get all available prompt templates
     */
    fun getAllTemplates(): Flow<List<PromptTemplateData>>

    /**
     * Get a specific template by ID
     */
    suspend fun getTemplate(id: String): PromptTemplateData?

    /**
     * Get templates by category
     */
    suspend fun getTemplatesByCategory(category: String): List<PromptTemplateData>

    /**
     * Get templates by tag
     */
    suspend fun getTemplatesByTag(tag: String): List<PromptTemplateData>

    /**
     * Save a new template or update existing one
     */
    suspend fun saveTemplate(template: PromptTemplateData)

    /**
     * Delete a template
     */
    suspend fun deleteTemplate(id: String)

    /**
     * Search templates by name or description
     */
    suspend fun searchTemplates(query: String): List<PromptTemplateData>

    /**
     * Get default system templates
     */
    suspend fun getDefaultTemplates(): List<PromptTemplateData>
}