package ai.julie.feature.toolmanagement.data

import ai.julie.feature.toolmanagement.domain.EnrichedTool
import ai.julie.feature.toolmanagement.domain.ToolCategory
import com.aallam.openai.api.chat.FunctionTool
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.chat.ToolType
import com.aallam.openai.api.core.Parameters
import kotbase.Document
import kotbase.MutableDocument
import kotlinx.serialization.json.*

// Database mapping constants
private const val FUNCTION_NAME = "function_name"
private const val FUNCTION_DESCRIPTION = "function_description"
private const val FUNCTION_PARAMETERS = "function_parameters"
private const val IS_ENABLED = "is_enabled"
private const val CATEGORY = "category"
private const val CREATED_AT = "created_at"
private const val UPDATED_AT = "updated_at"

fun EnrichedTool.toDocument(): MutableDocument {
    val doc = MutableDocument(id = this.function.name)
    
    // Store function details
    doc.setString(FUNCTION_NAME, this.function.name)
    doc.setString(FUNCTION_DESCRIPTION, this.function.description ?: "")
    
    // Convert parameters JsonObject to string for storage
    this.function.parameters?.let { params ->
        doc.setString(FUNCTION_PARAMETERS, params.toString())
    }
    
    // Store enriched properties
    doc.setBoolean(IS_ENABLED, this.isEnabled)
    doc.setString(CATEGORY, this.category.name)
    doc.setLong(CREATED_AT, this.createdAt)
    doc.setLong(UPDATED_AT, this.updatedAt)
    
    return doc
}

fun Document.toEnrichedTool(): EnrichedTool {
    val functionName = getString(FUNCTION_NAME) ?: ""
    val functionDescription = getString(FUNCTION_DESCRIPTION) ?: ""
    val parametersString = getString(FUNCTION_PARAMETERS)
    
    // Parse parameters back to Parameters
    val parameters = parametersString?.let {
        try {
            Parameters.buildJsonObject {
                Json.parseToJsonElement(it).jsonObject.forEach { (key, value) ->
                    put(key, value)
                }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    val function = FunctionTool(
        name = functionName,
        description = functionDescription,
        parameters = parameters
    )
    
    val tool = Tool(
        function = function,
        type = ToolType.Function
    )
    
    val categoryString = getString(CATEGORY) ?: ToolCategory.CUSTOM.name
    val category = try {
        ToolCategory.valueOf(categoryString)
    } catch (e: Exception) {
        ToolCategory.CUSTOM
    }
    
    return EnrichedTool(
        tool = tool,
        isEnabled = getBoolean(IS_ENABLED),
        category = category,
        createdAt = getLong(CREATED_AT),
        updatedAt = getLong(UPDATED_AT)
    )
}