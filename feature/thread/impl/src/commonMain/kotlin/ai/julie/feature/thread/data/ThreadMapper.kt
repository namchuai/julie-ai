package ai.julie.feature.thread.data

import ai.julie.feature.thread.domain.model.EnrichedThread
import com.aallam.openai.api.assistant.CodeInterpreterResources
import com.aallam.openai.api.assistant.FileSearchResources
import com.aallam.openai.api.assistant.ToolResources
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.api.vectorstore.VectorStoreId
import kotbase.Document
import kotbase.MutableArray
import kotbase.MutableDictionary
import kotbase.MutableDocument

// TODO: NamH clean this up. Should declare data class for each field
private const val OBJECT = "object"
private const val CREATED_AT = "created_at"
private const val UPDATED_AT = "updated_at"
private const val TOOL_RESOURCES = "tool_resources"
private const val METADATA = "metadata"
private const val MODEL_ID = "model_id"
private const val SAMPLING_PRESET_ID = "sampling_preset_id"
private const val FILE_SEARCH = "file_search"
private const val CODE_INTERPRETER = "code_interpreter"
private const val VECTOR_STORE_IDS = "vector_store_ids"
private const val FILE_IDS = "file_ids"

private fun FileSearchResources.toDictionary(): MutableDictionary? {
    if (vectorStoreIds.isNullOrEmpty()) return null
    val dict = MutableDictionary()
    val vectorStoreIdArray = MutableArray(vectorStoreIds!!.map { it.id })
    dict.setArray(VECTOR_STORE_IDS, vectorStoreIdArray)
    return dict
}

private fun CodeInterpreterResources.toDictionary(): MutableDictionary? {
    if (fileIds.isNullOrEmpty()) return null
    val dict = MutableDictionary()
    val fileIdArray = MutableArray(fileIds!!.map { it.id })
    dict.setArray(FILE_IDS, fileIdArray)
    return dict
}

private fun ToolResources.toDictionary(): MutableDictionary? {
    val toolResourcesDict = MutableDictionary()

    this.fileSearch?.toDictionary()?.let {
        toolResourcesDict.setDictionary(FILE_SEARCH, it)
    }

    this.codeInterpreter?.toDictionary()?.let {
        toolResourcesDict.setDictionary(CODE_INTERPRETER, it)
    }

    return if (toolResourcesDict.count > 0) toolResourcesDict else null
}

fun Thread.toDocument(): MutableDocument {
    val doc = MutableDocument(id = this.id.id)
    doc.setString(OBJECT, this.objectType ?: "thread")
    doc.setInt(CREATED_AT, this.createdAt)

    this.toolResources?.let { doc.setDictionary(TOOL_RESOURCES, it.toDictionary()) }
    doc.setDictionary(METADATA, MutableDictionary(this.metadata))

    return doc
}

fun Document.toThread(): Thread {
    val metadataMap =
        getDictionary(METADATA)?.toMap()?.mapValues { it.value?.toString() ?: "" } ?: emptyMap()

    val toolResourcesDict = getDictionary(TOOL_RESOURCES)
    val fileSearchDict = toolResourcesDict?.getDictionary(FILE_SEARCH)
    val codeInterpreterDict = toolResourcesDict?.getDictionary(CODE_INTERPRETER)

    val fileSearchResources =
        fileSearchDict?.getArray(VECTOR_STORE_IDS)?.toList()?.filterIsInstance<String>()
            ?.map { VectorStoreId(it) }?.let {
                FileSearchResources(vectorStoreIds = it)
            }

    val codeInterpreterResources =
        codeInterpreterDict?.getArray(FILE_IDS)?.toList()?.filterIsInstance<String>()
            ?.map { FileId(it) }?.let {
                CodeInterpreterResources(fileIds = it)
            }

    val toolResources = if (fileSearchResources != null || codeInterpreterResources != null) {
        ToolResources(codeInterpreter = codeInterpreterResources, fileSearch = fileSearchResources)
    } else {
        null
    }

    return Thread(
        id = ThreadId(this.id),
        objectType = getString(OBJECT) ?: "thread",
        createdAt = getInt(CREATED_AT),
        toolResources = toolResources,
        metadata = metadataMap
    )
}

// EnrichedThread conversion functions
fun EnrichedThread.toDocument(): MutableDocument {
    val doc = this.thread.toDocument()
    doc.setString(SAMPLING_PRESET_ID, this.samplingPresetId)
    doc.setString(MODEL_ID, this.modelId)
    doc.setInt(UPDATED_AT, this.updatedAt)
    return doc
}

fun Document.toEnrichedThread(): EnrichedThread {
    val thread = this.toThread()
    val modelId = getString(MODEL_ID) ?: ""
    val updatedAt = getInt(UPDATED_AT).takeIf { it != 0 } ?: thread.createdAt
    return EnrichedThread(
        thread = thread,
        samplingPresetId = getString(SAMPLING_PRESET_ID),
        modelId = modelId,
        updatedAt = updatedAt
    )
}
