package ai.julie.feature.modelconfig.domain.gguf

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
import kotbase.Document
import kotbase.MutableArray
import kotbase.MutableDocument

data class LlamaModelMetadata(
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
    val ropeDimensionCount: DimensionCount,
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

    override val architecture: Architecture = Architecture(value = SupportedArchitecture.LLAMA)
    
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
            setLong(ropeDimensionCount.key, ropeDimensionCount.value.toLong())
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

    companion object {
        fun fromDocument(document: Document): LlamaModelMetadata {
            val llamaArch = SupportedArchitecture.LLAMA
            
            return LlamaModelMetadata(
                quantizationVersion = QuantizationVersion(document.getInt(QuantizationVersion.KEY).toUInt()),
                alignment = Alignment(document.getInt(Alignment.KEY).toUInt()),
                
                // General Information fields - Optional
                name = document.getString(Name.KEY)?.let { Name(it) },
                author = document.getString(Author.KEY)?.let { Author(it) },
                version = document.getString(Version.KEY)?.let { Version(it) },
                organization = document.getString(Organization.KEY)?.let { Organization(it) },
                basename = document.getString(Basename.KEY)?.let { Basename(it) },
                datasets = document.getArray(Datasets.KEY)?.toList()?.filterIsInstance<String>()?.let { Datasets(it) },
                description = document.getString(Description.KEY)?.let { Description(it) },
                doi = document.getString(Doi.KEY)?.let { Doi(it) },
                fileType = document.getInt(FileType.KEY).takeIf { it != 0 }?.let { fileTypeInt ->
                    val fileTypeValue = FileTypeValue.entries.find { it.value.toInt() == fileTypeInt }
                        ?: throw IllegalArgumentException("Invalid file type value: $fileTypeInt")
                    FileType(fileTypeValue)
                },
                fineTune = document.getString(FineTune.KEY)?.let { FineTune(it) },
                languages = document.getArray(Languages.KEY)?.toList()?.filterIsInstance<String>()?.let { Languages(it) },
                license = document.getString(License.KEY)?.let { License(it) },
                licenseLink = document.getString(LicenseLink.KEY)?.let { LicenseLink(it) },
                licenseName = document.getString(LicenseName.KEY)?.let { LicenseName(it) },
                quantizedBy = document.getString(QuantizedBy.KEY)?.let { QuantizedBy(it) },
                repoUrl = document.getString(RepoUrl.KEY)?.let { RepoUrl(it) },
                sizeLabel = document.getString(SizeLabel.KEY)?.let { SizeLabel(it) },
                tags = document.getArray(Tags.KEY)?.toList()?.filterIsInstance<String>()?.let { Tags(it) },
                url = document.getString(Url.KEY)?.let { Url(it) },
                uuid = document.getString(Uuid.KEY)?.let { Uuid(it) },
                
                // Source Information fields - Optional (BaseModel classes need IDs, but we don't know the exact structure from Document)
                baseModelAuthor = null, // TODO: BaseModel fields require ID parameter - complex to parse from Document
                baseModelCount = null, // TODO: BaseModel fields require ID parameter - complex to parse from Document  
                baseModelDoi = null, // TODO: BaseModel fields require ID parameter - complex to parse from Document
                baseModelName = null, // TODO: BaseModel fields require ID parameter - complex to parse from Document
                baseModelOrganization = null, // TODO: BaseModel fields require ID parameter - complex to parse from Document
                baseModelRepoUrl = null, // TODO: BaseModel fields require ID parameter - complex to parse from Document
                baseModelUrl = null, // TODO: BaseModel fields require ID parameter - complex to parse from Document
                baseModelUuid = null, // TODO: BaseModel fields require ID parameter - complex to parse from Document
                baseModelVersion = null, // TODO: BaseModel fields require ID parameter - complex to parse from Document
                sourceDoi = document.getString(SourceDoi.KEY)?.let { SourceDoi(it) },
                sourceRepoUrl = document.getString(SourceRepoUrl.KEY)?.let { SourceRepoUrl(it) },
                sourceUrl = document.getString(SourceUrl.KEY)?.let { SourceUrl(it) },
                sourceUuid = document.getString(SourceUuid.KEY)?.let { SourceUuid(it) },
                
                // LLM-specific properties - Some mandatory for LLAMA
                contextLength = ContextLength(llamaArch, document.getLong("${llamaArch.value}.context_length").toULong()),
                embeddingLength = EmbeddingLength(llamaArch, document.getLong("${llamaArch.value}.embedding_length").toULong()),
                blockCount = BlockCount(llamaArch, document.getLong("${llamaArch.value}.block_count").toULong()),
                feedForwardLength = FeedForwardLength(llamaArch, document.getLong("${llamaArch.value}.feed_forward_length").toULong()),
                useParallelResidual = document.getString("${llamaArch.value}.use_parallel_residual")?.let { UseParallelResidual(llamaArch, it.toBoolean()) },
                tensorDataLayout = document.getString("${llamaArch.value}.tensor_data_layout")?.let { TensorDataLayout(llamaArch, it) },
                expertCount = document.getLong("${llamaArch.value}.expert_count").takeIf { it != 0L }?.let { ExpertCount(llamaArch, it.toUInt()) },
                expertUsedCount = document.getLong("${llamaArch.value}.expert_used_count").takeIf { it != 0L }?.let { ExpertUsedCount(llamaArch, it.toUInt()) },
                
                // Attention-specific properties - Some mandatory for LLAMA
                headCount = HeadCount(llamaArch, document.getLong("${llamaArch.value}.attention.head_count").toULong()),
                headCountKv = document.getLong("${llamaArch.value}.attention.head_count_kv").takeIf { it != 0L }?.let { HeadCountKv(llamaArch, it.toULong()) },
                maxAlibiBias = document.getFloat("${llamaArch.value}.attention.max_alibi_bias").takeIf { it != 0f }?.let { MaxAlibiBias(llamaArch, it) },
                clampKqv = document.getFloat("${llamaArch.value}.attention.clamp_kqv").takeIf { it != 0f }?.let { ClampKqv(llamaArch, it) },
                layerNormEpsilon = document.getFloat("${llamaArch.value}.attention.layer_norm_epsilon").takeIf { it != 0f }?.let { LayerNormEpsilon(llamaArch, it) },
                layerNormRmsEpsilon = LayerNormRmsEpsilon(llamaArch, document.getFloat("${llamaArch.value}.attention.layer_norm_rms_epsilon")),
                keyLength = document.getLong("${llamaArch.value}.attention.key_length").takeIf { it != 0L }?.let { KeyLength(llamaArch, it.toUInt()) },
                valueLength = document.getLong("${llamaArch.value}.attention.value_length").takeIf { it != 0L }?.let { ValueLength(llamaArch, it.toUInt()) },
                
                // RoPE-specific properties - Some mandatory for LLAMA
                ropeDimensionCount = DimensionCount(llamaArch, document.getLong("${llamaArch.value}.rope.dimension_count").toULong()),
                ropeFreqBase = document.getFloat("${llamaArch.value}.rope.freq_base").takeIf { it != 0f }?.let { FreqBase(llamaArch, it) },
                ropeScaleLinear = document.getFloat("${llamaArch.value}.rope.scale_linear").takeIf { it != 0f }?.let { Scale(llamaArch, it) },
                
                // RoPE Scaling properties - All optional
                ropeScalingType = document.getString("${llamaArch.value}.rope.scaling.type")?.let { Type(llamaArch, RopeScalingType.valueOf(it.uppercase())) },
                ropeScalingFactor = document.getFloat("${llamaArch.value}.rope.scaling.factor").takeIf { it != 0f }?.let { Factor(llamaArch, it) },
                ropeScalingOriginalContextLength = document.getLong("${llamaArch.value}.rope.scaling.original_context_length").takeIf { it != 0L }?.let { OriginalContextLength(llamaArch, it.toUInt()) },
                ropeScalingFineTuned = document.getBoolean("${llamaArch.value}.rope.scaling.finetuned").takeIf { document.contains("${llamaArch.value}.rope.scaling.finetuned") }?.let { FineTuned(llamaArch, it) },
                
                // SSM-specific properties - All optional
                ssmConvKernel = document.getLong("${llamaArch.value}.ssm.conv_kernel").takeIf { it != 0L }?.let { ConvKernel(llamaArch, it.toUInt()) },
                ssmInnerSize = document.getLong("${llamaArch.value}.ssm.inner_size").takeIf { it != 0L }?.let { InnerSize(llamaArch, it.toUInt()) },
                ssmStateSize = document.getLong("${llamaArch.value}.ssm.state_size").takeIf { it != 0L }?.let { StateSize(llamaArch, it.toUInt()) },
                ssmTimeStepRank = document.getLong("${llamaArch.value}.ssm.time_step_rank").takeIf { it != 0L }?.let { TimeStepRank(llamaArch, it.toUInt()) },
                
                // Tokenizer properties - Optional
                tokenizerModel = document.getString(Model.KEY)?.let { Model(GgmlTokenizerModel.valueOf(it.uppercase())) },
                chatTemplate = document.getString(ChatTemplate.KEY)?.let { ChatTemplate(it) },
                bosToken = document.getString(BosToken.KEY)?.let { BosToken(it) },
                eosToken = document.getString(EosToken.KEY)?.let { EosToken(it) },
                paddingToken = document.getString(PaddingToken.KEY)?.let { PaddingToken(it) },
                separatorToken = document.getString(SeparatorToken.KEY)?.let { SeparatorToken(it) },
                unknownToken = document.getString(UnknownToken.KEY)?.let { UnknownToken(it) }
            )
        }
    }
}