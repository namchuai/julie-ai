package ai.julie.feature.modelconfig.domain.gguf.deepseek2

import ai.julie.feature.modelconfig.domain.gguf.GgufMetadata
import ai.julie.feature.modelconfig.domain.gguf.general.Alignment
import ai.julie.feature.modelconfig.domain.gguf.general.Architecture
import ai.julie.feature.modelconfig.domain.gguf.general.Author
import ai.julie.feature.modelconfig.domain.gguf.general.Basename
import ai.julie.feature.modelconfig.domain.gguf.general.Datasets
import ai.julie.feature.modelconfig.domain.gguf.general.Description
import ai.julie.feature.modelconfig.domain.gguf.general.Doi
import ai.julie.feature.modelconfig.domain.gguf.general.FileType
import ai.julie.feature.modelconfig.domain.gguf.general.FileTypeValue
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
import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture
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
import ai.julie.feature.modelconfig.domain.gguf.llm.BlockCount
import ai.julie.feature.modelconfig.domain.gguf.llm.ContextLength
import ai.julie.feature.modelconfig.domain.gguf.llm.EmbeddingLength
import ai.julie.feature.modelconfig.domain.gguf.llm.ExpertCount
import ai.julie.feature.modelconfig.domain.gguf.llm.ExpertUsedCount
import ai.julie.feature.modelconfig.domain.gguf.llm.FeedForwardLength
import ai.julie.feature.modelconfig.domain.gguf.llm.TensorDataLayout
import ai.julie.feature.modelconfig.domain.gguf.llm.UseParallelResidual
import ai.julie.feature.modelconfig.domain.gguf.llm.attention.ClampKqv
import ai.julie.feature.modelconfig.domain.gguf.llm.attention.HeadCount
import ai.julie.feature.modelconfig.domain.gguf.llm.attention.HeadCountKv
import ai.julie.feature.modelconfig.domain.gguf.llm.attention.KeyLength
import ai.julie.feature.modelconfig.domain.gguf.llm.attention.LayerNormEpsilon
import ai.julie.feature.modelconfig.domain.gguf.llm.attention.LayerNormRmsEpsilon
import ai.julie.feature.modelconfig.domain.gguf.llm.attention.MaxAlibiBias
import ai.julie.feature.modelconfig.domain.gguf.llm.attention.ValueLength
import ai.julie.feature.modelconfig.domain.gguf.llm.rope.DimensionCount
import ai.julie.feature.modelconfig.domain.gguf.llm.rope.FreqBase
import ai.julie.feature.modelconfig.domain.gguf.llm.rope.Scale
import ai.julie.feature.modelconfig.domain.gguf.llm.scaling.Factor
import ai.julie.feature.modelconfig.domain.gguf.llm.scaling.FineTuned
import ai.julie.feature.modelconfig.domain.gguf.llm.scaling.OriginalContextLength
import ai.julie.feature.modelconfig.domain.gguf.llm.scaling.RopeScalingType
import ai.julie.feature.modelconfig.domain.gguf.llm.scaling.Type
import ai.julie.feature.modelconfig.domain.gguf.llm.ssm.ConvKernel
import ai.julie.feature.modelconfig.domain.gguf.llm.ssm.InnerSize
import ai.julie.feature.modelconfig.domain.gguf.llm.ssm.StateSize
import ai.julie.feature.modelconfig.domain.gguf.llm.ssm.TimeStepRank
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.GgmlTokenizerModel
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.Model
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.BosToken
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.EosToken
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.PaddingToken
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.SeparatorToken
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.UnknownToken
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.other.ChatTemplate
import ai.julie.logging.Logger
import kotbase.Document
import kotbase.MutableArray
import kotbase.MutableDocument

data class Deepseek2ModelMetadata(
    override val quantizationVersion: QuantizationVersion,
    override val alignment: Alignment,

    // General Information fields (from GgufMetadataV2) - All optional
    override val name: Name? = null,
    override val author: Author? = null,
    override val version: Version? = null,
    override val organization: Organization? = null,
    override val basename: Basename? = null,
    override val datasets: Datasets? = null,
    override val description: Description? = null,
    override val doi: Doi? = null,
    override val fileType: FileType? = null,
    override val fineTune: FineTune? = null,
    override val languages: Languages? = null,
    override val license: License? = null,
    override val licenseLink: LicenseLink? = null,
    override val licenseName: LicenseName? = null,
    override val quantizedBy: QuantizedBy? = null,
    override val repoUrl: RepoUrl? = null,
    override val sizeLabel: SizeLabel? = null,
    override val tags: Tags? = null,
    override val url: Url? = null,
    override val uuid: Uuid? = null,

    // Source Information fields (from GgufMetadataV2) - All optional
    override val baseModelAuthor: BaseModelAuthor? = null,
    override val baseModelCount: BaseModelCount? = null,
    override val baseModelDoi: BaseModelDoi? = null,
    override val baseModelName: BaseModelName? = null,
    override val baseModelOrganization: BaseModelOrganization? = null,
    override val baseModelRepoUrl: BaseModelRepoUrl? = null,
    override val baseModelUrl: BaseModelUrl? = null,
    override val baseModelUuid: BaseModelUuid? = null,
    override val baseModelVersion: BaseModelVersion? = null,
    override val sourceDoi: SourceDoi? = null,
    override val sourceRepoUrl: SourceRepoUrl? = null,
    override val sourceUrl: SourceUrl? = null,
    override val sourceUuid: SourceUuid? = null,

    // LLM-specific properties - Some mandatory for LLAMA
    val contextLength: ContextLength, // MANDATORY
    val embeddingLength: EmbeddingLength, // MANDATORY
    val blockCount: BlockCount, // MANDATORY
    val feedForwardLength: FeedForwardLength, // MANDATORY
    val useParallelResidual: UseParallelResidual? = null,
    val tensorDataLayout: TensorDataLayout? = null,
    val expertCount: ExpertCount? = null,
    val expertUsedCount: ExpertUsedCount? = null,

    // Attention-specific properties - Some mandatory for LLAMA
    val headCount: HeadCount,
    val headCountKv: HeadCountKv? = null,
    val maxAlibiBias: MaxAlibiBias? = null,
    val clampKqv: ClampKqv? = null,
    val layerNormEpsilon: LayerNormEpsilon? = null,
    val layerNormRmsEpsilon: LayerNormRmsEpsilon,
    val keyLength: KeyLength? = null,
    val valueLength: ValueLength? = null,

    // RoPE-specific properties - Some mandatory for LLAMA
    val ropeDimensionCount: DimensionCount? = null,
    val ropeFreqBase: FreqBase? = null,
    val ropeScaleLinear: Scale? = null, // Legacy key for older models

    // RoPE Scaling properties - All optional
    val ropeScalingType: Type? = null,
    val ropeScalingFactor: Factor? = null,
    val ropeScalingOriginalContextLength: OriginalContextLength? = null,
    val ropeScalingFineTuned: FineTuned? = null,

    // SSM-specific properties - All optional  
    val ssmConvKernel: ConvKernel? = null,
    val ssmInnerSize: InnerSize? = null,
    val ssmStateSize: StateSize? = null,
    val ssmTimeStepRank: TimeStepRank? = null,

    // Tokenizer properties - Optional
    val tokenizerModel: Model? = null,
    val chatTemplate: ChatTemplate? = null,
    val bosToken: BosToken? = null,
    val eosToken: EosToken? = null,
    val paddingToken: PaddingToken? = null,
    val separatorToken: SeparatorToken? = null,
    val unknownToken: UnknownToken? = null,
) : GgufMetadata {

    override val architecture: Architecture = Architecture(value = SupportedArchitecture.DEEPSEEK2)

    override fun toDocument(modelId: String): MutableDocument {
        return MutableDocument(modelId).apply {
            // Architecture and core fields
            setString(architecture.key, architecture.value.value)
            setInt(quantizationVersion.key, quantizationVersion.value.toInt())
            setInt(alignment.key, alignment.value.toInt())

            // General Information fields
            name?.let { setString(it.key, it.value) }
            author?.let { setString(it.key, it.value) }
            version?.let { setString(it.key, it.value) }
            organization?.let { setString(it.key, it.value) }
            basename?.let { setString(it.key, it.value) }
            description?.let { setString(it.key, it.value) }
            doi?.let { setString(it.key, it.value) }
            fineTune?.let { setString(it.key, it.value) }
            quantizedBy?.let { setString(it.key, it.value) }
            sizeLabel?.let { setString(it.key, it.value) }
            license?.let { setString(it.key, it.value) }
            licenseName?.let { setString(it.key, it.value) }
            licenseLink?.let { setString(it.key, it.value) }
            url?.let { setString(it.key, it.value) }
            uuid?.let { setString(it.key, it.value) }
            repoUrl?.let { setString(it.key, it.value) }
            fileType?.let { setInt(it.key, it.value.value.toInt()) }

            // Arrays
            tags?.let { setArray(it.key, MutableArray(it.value)) }
            languages?.let { setArray(it.key, MutableArray(it.value)) }
            datasets?.let { setArray(it.key, MutableArray(it.value)) }

            // Source Information
            sourceUrl?.let { setString(it.key, it.value) }
            sourceDoi?.let { setString(it.key, it.value) }
            sourceUuid?.let { setString(it.key, it.value) }
            sourceRepoUrl?.let { setString(it.key, it.value) }

            // Base Model Information
            baseModelCount?.let { setLong(it.key, it.value.toLong()) }
            baseModelName?.let { setString(it.key, it.value) }
            baseModelAuthor?.let { setString(it.key, it.value) }
            baseModelVersion?.let { setString(it.key, it.value) }
            baseModelOrganization?.let { setString(it.key, it.value) }
            baseModelUrl?.let { setString(it.key, it.value) }
            baseModelDoi?.let { setString(it.key, it.value) }
            baseModelUuid?.let { setString(it.key, it.value) }
            baseModelRepoUrl?.let { setString(it.key, it.value) }

            // LLM-specific fields
            setLong(contextLength.key, contextLength.value.toLong())
            setLong(embeddingLength.key, embeddingLength.value.toLong())
            setLong(blockCount.key, blockCount.value.toLong())
            setLong(feedForwardLength.key, feedForwardLength.value.toLong())
            useParallelResidual?.let { setBoolean(it.key, it.value) }
            tensorDataLayout?.let { setString(it.key, it.value) }
            expertCount?.let { setLong(it.key, it.value.toLong()) }
            expertUsedCount?.let { setLong(it.key, it.value.toLong()) }

            // Attention properties
            setLong(headCount.key, headCount.value.toLong())
            headCountKv?.let { setLong(it.key, it.value.toLong()) }
            maxAlibiBias?.let { setFloat(it.key, it.value) }
            clampKqv?.let { setFloat(it.key, it.value) }
            layerNormEpsilon?.let { setFloat(it.key, it.value) }
            setFloat(layerNormRmsEpsilon.key, layerNormRmsEpsilon.value)
            keyLength?.let { setLong(it.key, it.value.toLong()) }
            valueLength?.let { setLong(it.key, it.value.toLong()) }

            // RoPE properties
            ropeDimensionCount?.let {
                setLong(
                    ropeDimensionCount.key,
                    ropeDimensionCount.value.toLong()
                )
            }
            ropeFreqBase?.let { setFloat(it.key, it.value) }
            ropeScaleLinear?.let { setFloat(it.key, it.value) }
            ropeScalingType?.let { setString(it.key, it.value.name) }
            ropeScalingFactor?.let { setFloat(it.key, it.value) }
            ropeScalingOriginalContextLength?.let { setLong(it.key, it.value.toLong()) }
            ropeScalingFineTuned?.let { setBoolean(it.key, it.value) }

            // SSM properties
            ssmConvKernel?.let { setLong(it.key, it.value.toLong()) }
            ssmInnerSize?.let { setLong(it.key, it.value.toLong()) }
            ssmStateSize?.let { setLong(it.key, it.value.toLong()) }
            ssmTimeStepRank?.let { setLong(it.key, it.value.toLong()) }

            // Tokenizer properties
            tokenizerModel?.let { setString(it.key, it.value.value) }
            chatTemplate?.let { setString(it.key, it.value) }
            bosToken?.let { setString(it.key, it.value) }
            eosToken?.let { setString(it.key, it.value) }
            paddingToken?.let { setString(it.key, it.value) }
            separatorToken?.let { setString(it.key, it.value) }
            unknownToken?.let { setString(it.key, it.value) }
        }
    }

    companion object Companion {
        private const val TAG = "Deepseek2ModelMetadata"
        
        fun fromDocument(document: Document): Deepseek2ModelMetadata {
            val deepseek2Arch = SupportedArchitecture.DEEPSEEK2
            
            Logger.d("[$TAG] Starting document parsing for architecture: ${deepseek2Arch.value}")
            Logger.d("[$TAG] Document keys: ${document.keys}")

            return try {
                Logger.d("[$TAG] Parsing quantizationVersion...")
                val quantizationVersion = QuantizationVersion(
                    document.getInt(QuantizationVersion.KEY).toUInt()
                )
                Logger.d("[$TAG] quantizationVersion: ${quantizationVersion.value}")
                
                Logger.d("[$TAG] Parsing alignment...")
                val alignment = Alignment(document.getInt(Alignment.KEY).toUInt())
                Logger.d("[$TAG] alignment: ${alignment.value}")
                
                Logger.d("[$TAG] Parsing general fields...")
                
                Deepseek2ModelMetadata(
                quantizationVersion = quantizationVersion,
                alignment = alignment,

                // General Information fields - Optional
                name = document.getString(Name.KEY)?.let { Name(it) },
                author = document.getString(Author.KEY)?.let { Author(it) },
                version = document.getString(Version.KEY)?.let { Version(it) },
                organization = document.getString(Organization.KEY)?.let { Organization(it) },
                basename = document.getString(Basename.KEY)?.let { Basename(it) },
                datasets = document.getArray(Datasets.KEY)?.toList()?.filterIsInstance<String>()
                    ?.let { Datasets(it) },
                description = document.getString(Description.KEY)?.let { Description(it) },
                doi = document.getString(Doi.KEY)?.let { Doi(it) },
                fileType = document.getInt(FileType.KEY).takeIf { it != 0 }?.let { fileTypeInt ->
                    val fileTypeValue =
                        FileTypeValue.entries.find { it.value.toInt() == fileTypeInt }
                            ?: throw IllegalArgumentException("Invalid file type value: $fileTypeInt")
                    FileType(fileTypeValue)
                },
                fineTune = document.getString(FineTune.KEY)?.let { FineTune(it) },
                languages = document.getArray(Languages.KEY)?.toList()?.filterIsInstance<String>()
                    ?.let { Languages(it) },
                license = document.getString(License.KEY)?.let { License(it) },
                licenseLink = document.getString(LicenseLink.KEY)?.let { LicenseLink(it) },
                licenseName = document.getString(LicenseName.KEY)?.let { LicenseName(it) },
                quantizedBy = document.getString(QuantizedBy.KEY)?.let { QuantizedBy(it) },
                repoUrl = document.getString(RepoUrl.KEY)?.let { RepoUrl(it) },
                sizeLabel = document.getString(SizeLabel.KEY)?.let { SizeLabel(it) },
                tags = document.getArray(Tags.KEY)?.toList()?.filterIsInstance<String>()
                    ?.let { Tags(it) },
                url = document.getString(Url.KEY)?.let { Url(it) },
                uuid = document.getString(Uuid.KEY)?.let { Uuid(it) },

                // Source Information fields - Optional
                // BaseModelCount doesn't need an ID
                baseModelCount = document.getLong(BaseModelCount.KEY)
                    .takeIf { it != 0L }?.let { BaseModelCount(it.toUInt()) },
                // TODO: In the future, update the base data class to accept a list of base models 
                // when general.base_model.count > 1
                // For now, we only get the first base model (ID "0" or "1")
                baseModelAuthor = (document.getString("general.base_model.0.author") 
                    ?: document.getString("general.base_model.1.author"))
                    ?.let { BaseModelAuthor("0", it) },
                baseModelDoi = (document.getString("general.base_model.0.doi")
                    ?: document.getString("general.base_model.1.doi"))
                    ?.let { BaseModelDoi("0", it) },
                baseModelName = (document.getString("general.base_model.0.name")
                    ?: document.getString("general.base_model.1.name"))
                    ?.let { BaseModelName("0", it) },
                baseModelOrganization = (document.getString("general.base_model.0.organization")
                    ?: document.getString("general.base_model.1.organization"))
                    ?.let { BaseModelOrganization("0", it) },
                baseModelRepoUrl = (document.getString("general.base_model.0.repo_url")
                    ?: document.getString("general.base_model.1.repo_url"))
                    ?.let { BaseModelRepoUrl("0", it) },
                baseModelUrl = (document.getString("general.base_model.0.url")
                    ?: document.getString("general.base_model.1.url"))
                    ?.let { BaseModelUrl("0", it) },
                baseModelUuid = (document.getString("general.base_model.0.uuid")
                    ?: document.getString("general.base_model.1.uuid"))
                    ?.let { BaseModelUuid("0", it) },
                baseModelVersion = (document.getString("general.base_model.0.version")
                    ?: document.getString("general.base_model.1.version"))
                    ?.let { BaseModelVersion("0", it) },
                sourceDoi = document.getString(SourceDoi.KEY)?.let { SourceDoi(it) },
                sourceRepoUrl = document.getString(SourceRepoUrl.KEY)?.let { SourceRepoUrl(it) },
                sourceUrl = document.getString(SourceUrl.KEY)?.let { SourceUrl(it) },
                sourceUuid = document.getString(SourceUuid.KEY)?.let { SourceUuid(it) },

                // LLM-specific properties - Some mandatory for DEEPSEEK2
                contextLength = run {
                    Logger.d("[$TAG] Parsing contextLength with key: ${deepseek2Arch.value}.context_length")
                    val value = document.getLong("${deepseek2Arch.value}.context_length")
                    Logger.d("[$TAG] contextLength raw value: $value")
                    ContextLength(deepseek2Arch, value.toULong())
                },
                embeddingLength = run {
                    Logger.d("[$TAG] Parsing embeddingLength with key: ${deepseek2Arch.value}.embedding_length")
                    val value = document.getLong("${deepseek2Arch.value}.embedding_length")
                    Logger.d("[$TAG] embeddingLength raw value: $value")
                    EmbeddingLength(deepseek2Arch, value.toULong())
                },
                blockCount = run {
                    Logger.d("[$TAG] Parsing blockCount with key: ${deepseek2Arch.value}.block_count")
                    val value = document.getLong("${deepseek2Arch.value}.block_count")
                    Logger.d("[$TAG] blockCount raw value: $value")
                    BlockCount(deepseek2Arch, value.toULong())
                },
                feedForwardLength = run {
                    Logger.d("[$TAG] Parsing feedForwardLength with key: ${deepseek2Arch.value}.feed_forward_length")
                    val value = document.getLong("${deepseek2Arch.value}.feed_forward_length")
                    Logger.d("[$TAG] feedForwardLength raw value: $value")
                    FeedForwardLength(deepseek2Arch, value.toULong())
                },
                useParallelResidual = document.getString("${deepseek2Arch.value}.use_parallel_residual")
                    ?.let { UseParallelResidual(deepseek2Arch, it.toBoolean()) },
                tensorDataLayout = document.getString("${deepseek2Arch.value}.tensor_data_layout")
                    ?.let { TensorDataLayout(deepseek2Arch, it) },
                expertCount = document.getLong("${deepseek2Arch.value}.expert_count")
                    .takeIf { it != 0L }?.let { ExpertCount(deepseek2Arch, it.toUInt()) },
                expertUsedCount = document.getLong("${deepseek2Arch.value}.expert_used_count")
                    .takeIf { it != 0L }?.let { ExpertUsedCount(deepseek2Arch, it.toUInt()) },

                // Attention-specific properties - Some mandatory for DEEPSEEK2
                headCount = run {
                    Logger.d("[$TAG] Parsing headCount with key: ${deepseek2Arch.value}.attention.head_count")
                    val value = document.getLong("${deepseek2Arch.value}.attention.head_count")
                    Logger.d("[$TAG] headCount raw value: $value")
                    HeadCount(deepseek2Arch, value.toULong())
                },
                headCountKv = document.getLong("${deepseek2Arch.value}.attention.head_count_kv")
                    .takeIf { it != 0L }?.let { HeadCountKv(deepseek2Arch, it.toULong()) },
                maxAlibiBias = document.getFloat("${deepseek2Arch.value}.attention.max_alibi_bias")
                    .takeIf { it != 0f }?.let { MaxAlibiBias(deepseek2Arch, it) },
                clampKqv = document.getFloat("${deepseek2Arch.value}.attention.clamp_kqv")
                    .takeIf { it != 0f }?.let { ClampKqv(deepseek2Arch, it) },
                layerNormEpsilon = document.getFloat("${deepseek2Arch.value}.attention.layer_norm_epsilon")
                    .takeIf { it != 0f }?.let { LayerNormEpsilon(deepseek2Arch, it) },
                layerNormRmsEpsilon = run {
                    Logger.d("[$TAG] Parsing layerNormRmsEpsilon with key: ${deepseek2Arch.value}.attention.layer_norm_rms_epsilon")
                    val value = document.getFloat("${deepseek2Arch.value}.attention.layer_norm_rms_epsilon")
                    Logger.d("[$TAG] layerNormRmsEpsilon raw value: $value")
                    LayerNormRmsEpsilon(deepseek2Arch, value)
                },
                keyLength = document.getLong("${deepseek2Arch.value}.attention.key_length")
                    .takeIf { it != 0L }?.let { KeyLength(deepseek2Arch, it.toUInt()) },
                valueLength = document.getLong("${deepseek2Arch.value}.attention.value_length")
                    .takeIf { it != 0L }?.let { ValueLength(deepseek2Arch, it.toUInt()) },

                // RoPE-specific properties - Some mandatory for DEEPSEEK2
                ropeDimensionCount = run {
                    Logger.d("[$TAG] Parsing ropeDimensionCount with key: ${deepseek2Arch.value}.rope.dimension_count")
                    val value = document.getLong("${deepseek2Arch.value}.rope.dimension_count")
                    Logger.d("[$TAG] ropeDimensionCount raw value: $value")
                    if (value != 0L) {
                        DimensionCount(deepseek2Arch, value.toULong())
                    } else {
                        null
                    }
                },
                ropeFreqBase = document.getFloat("${deepseek2Arch.value}.rope.freq_base")
                    .takeIf { it != 0f }?.let { FreqBase(deepseek2Arch, it) },
                ropeScaleLinear = document.getFloat("${deepseek2Arch.value}.rope.scale_linear")
                    .takeIf { it != 0f }?.let { Scale(deepseek2Arch, it) },

                // RoPE Scaling properties - All optional
                ropeScalingType = document.getString("${deepseek2Arch.value}.rope.scaling.type")
                    ?.let { Type(deepseek2Arch, RopeScalingType.valueOf(it.uppercase())) },
                ropeScalingFactor = document.getFloat("${deepseek2Arch.value}.rope.scaling.factor")
                    .takeIf { it != 0f }?.let { Factor(deepseek2Arch, it) },
                ropeScalingOriginalContextLength = document.getLong("${deepseek2Arch.value}.rope.scaling.original_context_length")
                    .takeIf { it != 0L }?.let { OriginalContextLength(deepseek2Arch, it.toUInt()) },
                ropeScalingFineTuned = document.getBoolean("${deepseek2Arch.value}.rope.scaling.finetuned")
                    .takeIf { document.contains("${deepseek2Arch.value}.rope.scaling.finetuned") }
                    ?.let { FineTuned(deepseek2Arch, it) },

                // SSM-specific properties - All optional
                ssmConvKernel = document.getLong("${deepseek2Arch.value}.ssm.conv_kernel")
                    .takeIf { it != 0L }?.let { ConvKernel(deepseek2Arch, it.toUInt()) },
                ssmInnerSize = document.getLong("${deepseek2Arch.value}.ssm.inner_size")
                    .takeIf { it != 0L }?.let { InnerSize(deepseek2Arch, it.toUInt()) },
                ssmStateSize = document.getLong("${deepseek2Arch.value}.ssm.state_size")
                    .takeIf { it != 0L }?.let { StateSize(deepseek2Arch, it.toUInt()) },
                ssmTimeStepRank = document.getLong("${deepseek2Arch.value}.ssm.time_step_rank")
                    .takeIf { it != 0L }?.let { TimeStepRank(deepseek2Arch, it.toUInt()) },

                // Tokenizer properties - Optional
                tokenizerModel = document.getString(Model.KEY)
                    ?.let { Model(GgmlTokenizerModel.valueOf(it.uppercase())) },
                chatTemplate = document.getString(ChatTemplate.KEY)?.let { ChatTemplate(it) },
                bosToken = document.getString(BosToken.KEY)?.let { BosToken(it) },
                eosToken = document.getString(EosToken.KEY)?.let { EosToken(it) },
                paddingToken = document.getString(PaddingToken.KEY)?.let { PaddingToken(it) },
                separatorToken = document.getString(SeparatorToken.KEY)?.let { SeparatorToken(it) },
                unknownToken = document.getString(UnknownToken.KEY)?.let { UnknownToken(it) }
            )
            } catch (e: Exception) {
                Logger.e("[$TAG] Error parsing document to Deepseek2ModelMetadata: ${e.message}")
                Logger.e("[$TAG] Exception stack trace: ${e.stackTraceToString()}")
                throw e
            }
        }
    }
}