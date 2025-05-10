package ai.julie.feature.modelconfig.domain.ggufreader

import ai.julie.feature.modelconfig.domain.gguf.general.Alignment
import ai.julie.feature.modelconfig.domain.gguf.general.Architecture
import ai.julie.feature.modelconfig.domain.gguf.general.Author
import ai.julie.feature.modelconfig.domain.gguf.general.Basename
import ai.julie.feature.modelconfig.domain.gguf.general.Datasets
import ai.julie.feature.modelconfig.domain.gguf.general.Description
import ai.julie.feature.modelconfig.domain.gguf.general.Doi
import ai.julie.feature.modelconfig.domain.gguf.general.FileType
import ai.julie.feature.modelconfig.domain.gguf.general.FineTune
import ai.julie.feature.modelconfig.domain.gguf.general.Languages
import ai.julie.feature.modelconfig.domain.gguf.general.License
import ai.julie.feature.modelconfig.domain.gguf.general.LicenseLink
import ai.julie.feature.modelconfig.domain.gguf.general.LicenseName
import ai.julie.feature.modelconfig.domain.gguf.general.Name
import ai.julie.feature.modelconfig.domain.gguf.general.Organization
import ai.julie.feature.modelconfig.domain.gguf.general.*
import ai.julie.feature.modelconfig.domain.gguf.general.source.*

internal object GgufMetadataFactory {
    
    // Patterns for dynamic keys  
    private val baseModelNamePattern = Regex("general\\.base_model\\.(\\d+)\\.name")
    private val baseModelAuthorPattern = Regex("general\\.base_model\\.(\\d+)\\.author")
    private val baseModelVersionPattern = Regex("general\\.base_model\\.(\\d+)\\.version")
    private val baseModelOrganizationPattern = Regex("general\\.base_model\\.(\\d+)\\.organization")
    private val baseModelUrlPattern = Regex("general\\.base_model\\.(\\d+)\\.url")
    private val baseModelDoiPattern = Regex("general\\.base_model\\.(\\d+)\\.doi")
    private val baseModelUuidPattern = Regex("general\\.base_model\\.(\\d+)\\.uuid")
    private val baseModelRepoUrlPattern = Regex("general\\.base_model\\.(\\d+)\\.repo_url")
    
    fun createGeneralInfo(key: String, value: Any): GeneralInfo? {
        return when (key) {
            Alignment.KEY -> value.toUIntOrNull()?.let { Alignment(it) }
            Architecture.KEY -> value.toString().toArchitectureOrNull()?.let { Architecture(it) }
            Author.KEY -> (value as? String)?.let { Author(it) }
            Basename.KEY -> (value as? String)?.let { Basename(it) }
            Datasets.KEY -> value.toStringListOrNull()?.let { Datasets(it) }
            Description.KEY -> (value as? String)?.let { Description(it) }
            Doi.KEY -> (value as? String)?.let { Doi(it) }
            FileType.KEY -> value.toFileTypeOrNull()?.let { FileType(it) }
            FineTune.KEY -> (value as? String)?.let { FineTune(it) }
            Languages.KEY -> value.toStringListOrNull()?.let { Languages(it) }
            License.KEY -> (value as? String)?.let { License(it) }
            LicenseLink.KEY -> (value as? String)?.let { LicenseLink(it) }
            LicenseName.KEY -> (value as? String)?.let { LicenseName(it) }
            Name.KEY -> (value as? String)?.let { Name(it) }
            Organization.KEY -> (value as? String)?.let { Organization(it) }
            QuantizationVersion.KEY -> value.toUIntOrNull()?.let { QuantizationVersion(it) }
            QuantizedBy.KEY -> (value as? String)?.let { QuantizedBy(it) }
            RepoUrl.KEY -> (value as? String)?.let { RepoUrl(it) }
            SizeLabel.KEY -> (value as? String)?.let { SizeLabel(it) }
            Tags.KEY -> value.toStringListOrNull()?.let { Tags(it) }
            Url.KEY -> (value as? String)?.let { Url(it) }
            Uuid.KEY -> (value as? String)?.let { Uuid(it) }
            else -> null
        }
    }
    
    fun createSourceInfo(key: String, value: Any): GeneralSourceInfo? {
        // Static keys first
        when (key) {
            BaseModelCount.KEY -> return value.toUIntOrNull()?.let { BaseModelCount(it) }
            SourceUrl.KEY -> return (value as? String)?.let { SourceUrl(it) }
            SourceDoi.KEY -> return (value as? String)?.let { SourceDoi(it) }
            SourceUuid.KEY -> return (value as? String)?.let { SourceUuid(it) }
            SourceRepoUrl.KEY -> return (value as? String)?.let { SourceRepoUrl(it) }
        }
        
        // Dynamic keys with patterns
        baseModelNamePattern.matchEntire(key)?.let { match ->
            val id = match.groupValues[1]
            return (value as? String)?.let { BaseModelName(id, it) }
        }
        
        baseModelAuthorPattern.matchEntire(key)?.let { match ->
            val id = match.groupValues[1]
            return (value as? String)?.let { BaseModelAuthor(id, it) }
        }
        
        baseModelVersionPattern.matchEntire(key)?.let { match ->
            val id = match.groupValues[1]
            return (value as? String)?.let { BaseModelVersion(id, it) }
        }
        
        baseModelOrganizationPattern.matchEntire(key)?.let { match ->
            val id = match.groupValues[1]
            return (value as? String)?.let { BaseModelOrganization(id, it) }
        }
        
        baseModelUrlPattern.matchEntire(key)?.let { match ->
            val id = match.groupValues[1]
            return (value as? String)?.let { BaseModelUrl(id, it) }
        }
        
        baseModelDoiPattern.matchEntire(key)?.let { match ->
            val id = match.groupValues[1]
            return (value as? String)?.let { BaseModelDoi(id, it) }
        }
        
        baseModelUuidPattern.matchEntire(key)?.let { match ->
            val id = match.groupValues[1]
            return (value as? String)?.let { BaseModelUuid(id, it) }
        }
        
        baseModelRepoUrlPattern.matchEntire(key)?.let { match ->
            val id = match.groupValues[1]
            return (value as? String)?.let { BaseModelRepoUrl(id, it) }
        }
        
        return null
    }
    
    // Extension functions for type conversions
    private fun Any.toUIntOrNull(): UInt? = when (this) {
        is Int -> toUInt()
        is Long -> toUInt()
        is UInt -> this
        else -> null
    }
    
    private fun Any.toStringListOrNull(): List<String>? = when (this) {
        is List<*> -> filterIsInstance<String>().takeIf { it.isNotEmpty() }
        else -> null
    }
    
    private fun String.toArchitectureOrNull(): SupportedArchitecture? {
        return SupportedArchitecture.entries.find { it.value == this }
    }
    
    private fun Any.toFileTypeOrNull(): FileTypeValue? = when (this) {
        is Int -> FileTypeValue.entries.find { it.value == toUInt() }
        is Long -> FileTypeValue.entries.find { it.value == toUInt() }
        is UInt -> FileTypeValue.entries.find { it.value == this }
        else -> null
    } ?: FileTypeValue.ALL_F32 // Default value if not found
}