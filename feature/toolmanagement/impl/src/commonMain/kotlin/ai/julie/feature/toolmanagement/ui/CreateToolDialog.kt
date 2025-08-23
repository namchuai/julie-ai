package ai.julie.feature.toolmanagement.ui

import ai.julie.core.designsystem.component.components.Text
import ai.julie.feature.toolmanagement.domain.EnrichedTool
import ai.julie.feature.toolmanagement.domain.ToolCategory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import com.aallam.openai.api.chat.FunctionTool
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.chat.ToolType
import com.aallam.openai.api.core.Parameters
import kotlinx.datetime.Clock
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

data class ParameterField(
    val name: String = "",
    val type: String = "string",
    val description: String = "",
    val isRequired: Boolean = false,
    val enumValues: List<String> = emptyList()
)

@Composable
fun CreateToolDialog(
    onDismiss: () -> Unit,
    onCreate: (EnrichedTool) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ToolCategory.CUSTOM) }
    val parameters = remember { mutableStateListOf<ParameterField>() }
    
    var showCategoryDropdown by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = Color(0xFF2D2D2D)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "Create New Tool",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // Basic Function Info
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Function Name") },
                    placeholder = { Text("e.g. get_weather") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Describe when and how to use this function") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                // Category Selection
                Box {
                    OutlinedButton(
                        onClick = { showCategoryDropdown = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Category: ${category.name}")
                    }
                    
                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false },
                        modifier = Modifier.background(Color(0xFF3D3D3D))
                    ) {
                        ToolCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    category = cat
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }
                
                
                // Parameters Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Parameters",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    FloatingActionButton(
                        onClick = {
                            parameters.add(ParameterField())
                        },
                        modifier = Modifier.width(40.dp).height(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Parameter")
                    }
                }
                
                // Parameters List
                if (parameters.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.CardDefaults.cardColors(
                            containerColor = Color(0xFF3D3D3D)
                        )
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(parameters) { parameter ->
                                ParameterFieldRow(
                                    parameter = parameter,
                                    onParameterChange = { updatedParam ->
                                        val index = parameters.indexOf(parameter)
                                        if (index != -1) {
                                            parameters[index] = updatedParam
                                        }
                                    },
                                    onDelete = {
                                        parameters.remove(parameter)
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (name.isNotBlank() && description.isNotBlank()) {
                                val tool = createToolFromInputs(
                                    name = name,
                                    description = description,
                                    parameters = parameters,
                                    category = category
                                )
                                onCreate(tool)
                            }
                        },
                        enabled = name.isNotBlank() && description.isNotBlank()
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}

@Composable
fun ParameterFieldRow(
    parameter: ParameterField,
    onParameterChange: (ParameterField) -> Unit,
    onDelete: () -> Unit
) {
    var showTypeDropdown by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    val availableTypes = listOf("string", "number", "boolean", "object", "array")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(0xFF4D4D4D)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            // Header row with parameter name, expand/collapse, and delete
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (parameter.name.isNotBlank()) parameter.name else "New Parameter",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                    
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Parameter")
                    }
                }
            }
            
            // Expandable content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Parameter name input
                    OutlinedTextField(
                        value = parameter.name,
                        onValueChange = { onParameterChange(parameter.copy(name = it)) },
                        label = { Text("Parameter Name") },
                        placeholder = { Text("e.g. location") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Parameter type
                    Box {
                        OutlinedButton(
                            onClick = { showTypeDropdown = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Type: ${parameter.type}")
                        }
                        
                        DropdownMenu(
                            expanded = showTypeDropdown,
                            onDismissRequest = { showTypeDropdown = false },
                            modifier = Modifier.background(Color(0xFF3D3D3D))
                        ) {
                            availableTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        onParameterChange(parameter.copy(type = type))
                                        showTypeDropdown = false
                                    }
                                )
                            }
                        }
                    }
                    
                    // Parameter description
                    OutlinedTextField(
                        value = parameter.description,
                        onValueChange = { onParameterChange(parameter.copy(description = it)) },
                        label = { Text("Description") },
                        placeholder = { Text("Describe this parameter") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                    
                    // Enum values (if type is string)
                    if (parameter.type == "string") {
                        var enumInput by remember { mutableStateOf("") }
                        
                        OutlinedTextField(
                            value = enumInput,
                            onValueChange = { enumInput = it },
                            label = { Text("Enum Values (comma-separated)") },
                            placeholder = { Text("e.g. celsius,fahrenheit") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            trailingIcon = {
                                if (enumInput.isNotBlank()) {
                                    TextButton(
                                        onClick = {
                                            val enumValues = enumInput.split(",")
                                                .map { it.trim() }
                                                .filter { it.isNotEmpty() }
                                            onParameterChange(parameter.copy(enumValues = enumValues))
                                            enumInput = ""
                                        }
                                    ) {
                                        Text("Add")
                                    }
                                }
                            }
                        )
                        
                        // Display current enum values
                        if (parameter.enumValues.isNotEmpty()) {
                            Text(
                                text = "Enum values: ${parameter.enumValues.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Required checkbox
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Required Parameter")
                        Checkbox(
                            checked = parameter.isRequired,
                            onCheckedChange = { onParameterChange(parameter.copy(isRequired = it)) }
                        )
                    }
                }
            }
        }
    }
}

private fun createToolFromInputs(
    name: String,
    description: String,
    parameters: List<ParameterField>,
    category: ToolCategory
): EnrichedTool {
    // Build the JSON schema for parameters
    val properties = buildJsonObject {
        parameters.forEach { param ->
            put(param.name, buildJsonObject {
                put("type", JsonPrimitive(param.type))
                put("description", JsonPrimitive(param.description))
                
                // Add enum values if present
                if (param.enumValues.isNotEmpty()) {
                    put("enum", buildJsonArray {
                        param.enumValues.forEach { value ->
                            add(JsonPrimitive(value))
                        }
                    })
                }
            })
        }
    }
    
    val requiredFields = parameters
        .filter { it.isRequired }
        .map { it.name }
    
    val parametersSchema = Parameters.buildJsonObject {
        put("type", JsonPrimitive("object"))
        put("properties", properties)
        if (requiredFields.isNotEmpty()) {
            put("required", buildJsonArray {
                requiredFields.forEach { field ->
                    add(JsonPrimitive(field))
                }
            })
        }
        put("additionalProperties", JsonPrimitive(false))
    }
    
    // Create the FunctionTool
    val functionTool = FunctionTool(
        name = name,
        description = description,
        parameters = parametersSchema
    )
    
    // Create the Tool
    val tool = Tool(
        function = functionTool,
        type = ToolType.Function
    )
    
    val now = Clock.System.now().toEpochMilliseconds()
    
    return EnrichedTool(
        tool = tool,
        isEnabled = true,
        category = category,
        createdAt = now,
        updatedAt = now
    )
}