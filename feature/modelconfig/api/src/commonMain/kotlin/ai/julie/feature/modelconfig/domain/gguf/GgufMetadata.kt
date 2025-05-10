package ai.julie.feature.modelconfig.domain.gguf

import kotbase.MutableDocument

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
import ai.julie.feature.modelconfig.domain.gguf.general.QuantizationVersion
import ai.julie.feature.modelconfig.domain.gguf.general.QuantizedBy
import ai.julie.feature.modelconfig.domain.gguf.general.RepoUrl
import ai.julie.feature.modelconfig.domain.gguf.general.SizeLabel
import ai.julie.feature.modelconfig.domain.gguf.general.Tags
import ai.julie.feature.modelconfig.domain.gguf.general.Url
import ai.julie.feature.modelconfig.domain.gguf.general.Uuid
import ai.julie.feature.modelconfig.domain.gguf.general.Version
import ai.julie.feature.modelconfig.domain.gguf.general.source.BaseModelAuthor
import ai.julie.feature.modelconfig.domain.gguf.general.source.BaseModelCount
import ai.julie.feature.modelconfig.domain.gguf.general.source.BaseModelDoi
import ai.julie.feature.modelconfig.domain.gguf.general.source.BaseModelName
import ai.julie.feature.modelconfig.domain.gguf.general.source.BaseModelOrganization
import ai.julie.feature.modelconfig.domain.gguf.general.source.BaseModelRepoUrl
import ai.julie.feature.modelconfig.domain.gguf.general.source.BaseModelUrl
import ai.julie.feature.modelconfig.domain.gguf.general.source.BaseModelUuid
import ai.julie.feature.modelconfig.domain.gguf.general.source.BaseModelVersion
import ai.julie.feature.modelconfig.domain.gguf.general.source.SourceDoi
import ai.julie.feature.modelconfig.domain.gguf.general.source.SourceRepoUrl
import ai.julie.feature.modelconfig.domain.gguf.general.source.SourceUrl
import ai.julie.feature.modelconfig.domain.gguf.general.source.SourceUuid

interface GgufMetadata {
    val architecture: Architecture
    val quantizationVersion: QuantizationVersion
    val alignment: Alignment

    // General Information - All optional
    val name: Name? get() = null
    val author: Author? get() = null
    val version: Version? get() = null
    val organization: Organization? get() = null
    val basename: Basename? get() = null
    val fineTune: FineTune? get() = null
    val description: Description? get() = null
    val quantizedBy: QuantizedBy? get() = null
    val sizeLabel: SizeLabel? get() = null
    val license: License? get() = null
    val licenseName: LicenseName? get() = null
    val licenseLink: LicenseLink? get() = null
    val url: Url? get() = null
    val doi: Doi? get() = null
    val uuid: Uuid? get() = null
    val repoUrl: RepoUrl? get() = null
    val tags: Tags? get() = null
    val languages: Languages? get() = null
    val datasets: Datasets? get() = null
    val fileType: FileType? get() = null

    // Source Information - All optional
    val sourceUrl: SourceUrl? get() = null
    val sourceDoi: SourceDoi? get() = null
    val sourceUuid: SourceUuid? get() = null
    val sourceRepoUrl: SourceRepoUrl? get() = null

    // Base Model Information - All optional  
    val baseModelCount: BaseModelCount? get() = null
    val baseModelName: BaseModelName? get() = null
    val baseModelAuthor: BaseModelAuthor? get() = null
    val baseModelVersion: BaseModelVersion? get() = null
    val baseModelOrganization: BaseModelOrganization? get() = null
    val baseModelUrl: BaseModelUrl? get() = null
    val baseModelDoi: BaseModelDoi? get() = null
    val baseModelUuid: BaseModelUuid? get() = null
    val baseModelRepoUrl: BaseModelRepoUrl? get() = null

    fun toDocument(modelId: String): MutableDocument
}
