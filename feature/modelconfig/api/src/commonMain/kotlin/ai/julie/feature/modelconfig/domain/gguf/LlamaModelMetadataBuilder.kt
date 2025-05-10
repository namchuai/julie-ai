package ai.julie.feature.modelconfig.domain.gguf

import ai.julie.feature.modelconfig.domain.gguf.general.Alignment
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
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.Tokens
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.BosToken
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.BosTokenId
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.EosToken
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.EosTokenId
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.PaddingToken
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.PaddingTokenId
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.SeparatorToken
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.SeparatorTokenId
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.UnknownToken
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens.UnknownTokenId
import ai.julie.feature.modelconfig.domain.gguf.tokenizer.other.ChatTemplate

class LlamaModelMetadataBuilder : GgufMetadataBuilder {
    // Mandatory fields - must be set before build()
    private var quantizationVersion: QuantizationVersion? = null
    private var alignment: Alignment? = null
    private var contextLength: ContextLength? = null
    private var embeddingLength: EmbeddingLength? = null
    private var blockCount: BlockCount? = null
    private var feedForwardLength: FeedForwardLength? = null
    private var headCount: HeadCount? = null
    private var layerNormRmsEpsilon: LayerNormRmsEpsilon? = null
    private var ropeDimensionCount: DimensionCount? = null

    // General Information fields (from GgufMetadataV2)
    private var author: Author? = null
    private var basename: Basename? = null
    private var datasets: Datasets? = null
    private var description: Description? = null
    private var doi: Doi? = null
    private var fileType: FileType? = null
    private var fineTune: FineTune? = null
    private var languages: Languages? = null
    private var license: License? = null
    private var licenseLink: LicenseLink? = null
    private var licenseName: LicenseName? = null
    private var name: Name? = null
    private var organization: Organization? = null
    private var quantizedBy: QuantizedBy? = null
    private var repoUrl: RepoUrl? = null
    private var sizeLabel: SizeLabel? = null
    private var tags: Tags? = null
    private var url: Url? = null
    private var uuid: Uuid? = null
    private var version: Version? = null

    // Source Information fields
    private var baseModelAuthor: BaseModelAuthor? = null
    private var baseModelCount: BaseModelCount? = null
    private var baseModelDoi: BaseModelDoi? = null
    private var baseModelName: BaseModelName? = null
    private var baseModelOrganization: BaseModelOrganization? = null
    private var baseModelRepoUrl: BaseModelRepoUrl? = null
    private var baseModelUrl: BaseModelUrl? = null
    private var baseModelUuid: BaseModelUuid? = null
    private var baseModelVersion: BaseModelVersion? = null
    private var sourceDoi: SourceDoi? = null
    private var sourceRepoUrl: SourceRepoUrl? = null
    private var sourceUrl: SourceUrl? = null
    private var sourceUuid: SourceUuid? = null

    // Optional LLM fields
    private var useParallelResidual: UseParallelResidual? = null
    private var tensorDataLayout: TensorDataLayout? = null
    private var expertCount: ExpertCount? = null
    private var expertUsedCount: ExpertUsedCount? = null

    // Attention properties
    private var headCountKv: HeadCountKv? = null
    private var maxAlibiBias: MaxAlibiBias? = null
    private var clampKqv: ClampKqv? = null
    private var layerNormEpsilon: LayerNormEpsilon? = null
    private var keyLength: KeyLength? = null
    private var valueLength: ValueLength? = null

    // RoPE properties
    private var ropeFreqBase: FreqBase? = null
    private var ropeScaleLinear: Scale? = null

    // RoPE Scaling properties
    private var ropeScalingType: Type? = null
    private var ropeScalingFactor: Factor? = null
    private var ropeScalingOriginalContextLength: OriginalContextLength? = null
    private var ropeScalingFineTuned: FineTuned? = null

    // SSM properties
    private var ssmConvKernel: ConvKernel? = null
    private var ssmInnerSize: InnerSize? = null
    private var ssmStateSize: StateSize? = null
    private var ssmTimeStepRank: TimeStepRank? = null

    // Tokenizer properties
    private var tokenizerModel: Model? = null
    private var chatTemplate: ChatTemplate? = null
    private var tokens: Tokens? = null
    private var bosToken: BosToken? = null
    private var eosToken: EosToken? = null
    private var paddingToken: PaddingToken? = null
    private var separatorToken: SeparatorToken? = null
    private var unknownToken: UnknownToken? = null
    
    // Token IDs for resolution
    private var bosTokenId: BosTokenId? = null
    private var eosTokenId: EosTokenId? = null
    private var paddingTokenId: PaddingTokenId? = null
    private var separatorTokenId: SeparatorTokenId? = null
    private var unknownTokenId: UnknownTokenId? = null

    /**
     * Set a field based on the key and value from GGUF metadata
     */
    override fun setField(key: String, value: Any): GgufMetadataBuilder {
        when (key) {
            // Mandatory fields
            QuantizationVersion.KEY -> quantizationVersion = (value as? Number)?.toLong()?.toUInt()?.let { QuantizationVersion(it) }
            Alignment.KEY -> alignment = (value as? Number)?.toLong()?.toUInt()?.let { Alignment(it) }
            ContextLength(SupportedArchitecture.LLAMA, 0u).key -> contextLength =
                (value as? Number)?.toLong()?.toULong()?.let { ContextLength(SupportedArchitecture.LLAMA, it) }

            EmbeddingLength(SupportedArchitecture.LLAMA, 0u).key -> embeddingLength =
                (value as? Number)?.toLong()?.toULong()?.let { EmbeddingLength(SupportedArchitecture.LLAMA, it) }

            BlockCount(SupportedArchitecture.LLAMA, 0u).key -> blockCount = 
                (value as? Number)?.toLong()?.toULong()?.let { BlockCount(SupportedArchitecture.LLAMA, it) }
            FeedForwardLength(SupportedArchitecture.LLAMA, 0u).key -> feedForwardLength =
                (value as? Number)?.toLong()?.toULong()?.let { FeedForwardLength(SupportedArchitecture.LLAMA, it) }

            HeadCount(SupportedArchitecture.LLAMA, 0u).key -> headCount = 
                (value as? Number)?.toLong()?.toULong()?.let { HeadCount(SupportedArchitecture.LLAMA, it) }
            LayerNormRmsEpsilon(SupportedArchitecture.LLAMA, 0.0f).key -> layerNormRmsEpsilon =
                (value as? Number)?.toFloat()?.let { LayerNormRmsEpsilon(SupportedArchitecture.LLAMA, it) }

            DimensionCount(SupportedArchitecture.LLAMA, 0u).key -> ropeDimensionCount =
                (value as? Number)?.toLong()?.toULong()?.let { DimensionCount(SupportedArchitecture.LLAMA, it) }

            // General Information fields
            Author.KEY -> author = (value as? String)?.let { Author(it) }
            Basename.KEY -> basename = (value as? String)?.let { Basename(it) }
            Datasets.KEY -> datasets = (value as? List<*>)?.filterIsInstance<String>()?.let { Datasets(it) }
            Description.KEY -> description = (value as? String)?.let { Description(it) }
            Doi.KEY -> doi = (value as? String)?.let { Doi(it) }
            FileType.KEY -> fileType = (value as? Number)?.toLong()?.toUInt()?.let { FileTypeValue.entries.find { ft -> ft.value == it } }?.let { FileType(it) }
            FineTune.KEY -> fineTune = (value as? String)?.let { FineTune(it) }
            Languages.KEY -> languages = (value as? List<*>)?.filterIsInstance<String>()?.let { Languages(it) }
            License.KEY -> license = (value as? String)?.let { License(it) }
            LicenseLink.KEY -> licenseLink = (value as? String)?.let { LicenseLink(it) }
            LicenseName.KEY -> licenseName = (value as? String)?.let { LicenseName(it) }
            Name.KEY -> name = (value as? String)?.let { Name(it) }
            Organization.KEY -> organization = (value as? String)?.let { Organization(it) }
            QuantizedBy.KEY -> quantizedBy = (value as? String)?.let { QuantizedBy(it) }
            RepoUrl.KEY -> repoUrl = (value as? String)?.let { RepoUrl(it) }
            SizeLabel.KEY -> sizeLabel = (value as? String)?.let { SizeLabel(it) }
            Tags.KEY -> tags = (value as? List<*>)?.filterIsInstance<String>()?.let { Tags(it) }
            Url.KEY -> url = (value as? String)?.let { Url(it) }
            Uuid.KEY -> uuid = (value as? String)?.let { Uuid(it) }
            Version.KEY -> version = (value as? String)?.let { Version(it) }

            // Source Information fields
            BaseModelCount.KEY -> baseModelCount = (value as? Number)?.toLong()?.toUInt()?.let { BaseModelCount(it) }
            SourceDoi.KEY -> sourceDoi = (value as? String)?.let { SourceDoi(it) }
            SourceRepoUrl.KEY -> sourceRepoUrl = (value as? String)?.let { SourceRepoUrl(it) }
            SourceUrl.KEY -> sourceUrl = (value as? String)?.let { SourceUrl(it) }
            SourceUuid.KEY -> sourceUuid = (value as? String)?.let { SourceUuid(it) }

            // Optional LLM fields
            UseParallelResidual(SupportedArchitecture.LLAMA, false).key -> useParallelResidual = (value as? Boolean)?.let { 
                UseParallelResidual(SupportedArchitecture.LLAMA, it) 
            }
            TensorDataLayout(SupportedArchitecture.LLAMA, "").key -> tensorDataLayout = (value as? String)?.let { 
                TensorDataLayout(SupportedArchitecture.LLAMA, it) 
            }
            ExpertCount(SupportedArchitecture.LLAMA, 0u).key -> expertCount = (value as? Number)?.toLong()?.toUInt()?.let { 
                ExpertCount(SupportedArchitecture.LLAMA, it) 
            }
            ExpertUsedCount(SupportedArchitecture.LLAMA, 0u).key -> expertUsedCount = (value as? Number)?.toLong()?.toUInt()?.let { 
                ExpertUsedCount(SupportedArchitecture.LLAMA, it) 
            }

            // Attention properties
            HeadCountKv(SupportedArchitecture.LLAMA, 0u).key -> headCountKv = (value as? Number)?.toLong()?.toULong()?.let { 
                HeadCountKv(SupportedArchitecture.LLAMA, it) 
            }
            MaxAlibiBias(SupportedArchitecture.LLAMA, 0.0f).key -> maxAlibiBias = (value as? Number)?.toFloat()?.let { 
                MaxAlibiBias(SupportedArchitecture.LLAMA, it) 
            }
            ClampKqv(SupportedArchitecture.LLAMA, 0.0f).key -> clampKqv = (value as? Number)?.toFloat()?.let { 
                ClampKqv(SupportedArchitecture.LLAMA, it) 
            }
            LayerNormEpsilon(SupportedArchitecture.LLAMA, 0.0f).key -> layerNormEpsilon = (value as? Number)?.toFloat()?.let { 
                LayerNormEpsilon(SupportedArchitecture.LLAMA, it) 
            }
            KeyLength(SupportedArchitecture.LLAMA, 0u).key -> keyLength = (value as? Number)?.toLong()?.toUInt()?.let { 
                KeyLength(SupportedArchitecture.LLAMA, it) 
            }
            ValueLength(SupportedArchitecture.LLAMA, 0u).key -> valueLength = (value as? Number)?.toLong()?.toUInt()?.let { 
                ValueLength(SupportedArchitecture.LLAMA, it) 
            }

            // RoPE properties
            FreqBase(SupportedArchitecture.LLAMA, 0.0f).key -> ropeFreqBase = (value as? Number)?.toFloat()?.let { 
                FreqBase(SupportedArchitecture.LLAMA, it) 
            }
            Scale(SupportedArchitecture.LLAMA, 0.0f).key -> ropeScaleLinear = (value as? Number)?.toFloat()?.let { 
                Scale(SupportedArchitecture.LLAMA, it) 
            }

            // RoPE Scaling properties
            Type(SupportedArchitecture.LLAMA, RopeScalingType.NONE).key -> ropeScalingType = (value as? String)?.let { typeStr ->
                val ropeType = when (typeStr.lowercase()) {
                    "linear" -> RopeScalingType.LINEAR
                    "yarn" -> RopeScalingType.YARN
                    else -> RopeScalingType.NONE
                }
                Type(SupportedArchitecture.LLAMA, ropeType)
            }
            Factor(SupportedArchitecture.LLAMA, 0.0f).key -> ropeScalingFactor = (value as? Number)?.toFloat()?.let { 
                Factor(SupportedArchitecture.LLAMA, it) 
            }
            OriginalContextLength(SupportedArchitecture.LLAMA, 0u).key -> ropeScalingOriginalContextLength = (value as? Number)?.toLong()?.toUInt()?.let { 
                OriginalContextLength(SupportedArchitecture.LLAMA, it) 
            }
            FineTuned(SupportedArchitecture.LLAMA, false).key -> ropeScalingFineTuned = (value as? Boolean)?.let { 
                FineTuned(SupportedArchitecture.LLAMA, it) 
            }

            // SSM properties
            ConvKernel(SupportedArchitecture.LLAMA, 0u).key -> ssmConvKernel = (value as? Number)?.toLong()?.toUInt()?.let { 
                ConvKernel(SupportedArchitecture.LLAMA, it) 
            }
            InnerSize(SupportedArchitecture.LLAMA, 0u).key -> ssmInnerSize = (value as? Number)?.toLong()?.toUInt()?.let { 
                InnerSize(SupportedArchitecture.LLAMA, it) 
            }
            StateSize(SupportedArchitecture.LLAMA, 0u).key -> ssmStateSize = (value as? Number)?.toLong()?.toUInt()?.let { 
                StateSize(SupportedArchitecture.LLAMA, it) 
            }
            TimeStepRank(SupportedArchitecture.LLAMA, 0u).key -> ssmTimeStepRank = (value as? Number)?.toLong()?.toUInt()?.let { 
                TimeStepRank(SupportedArchitecture.LLAMA, it) 
            }

            // Tokenizer properties
            Model.KEY -> tokenizerModel = (value as? String)?.let { modelStr ->
                val tokenizer = when (modelStr.lowercase()) {
                    "llama" -> GgmlTokenizerModel.LLAMA
                    "replit" -> GgmlTokenizerModel.REPLIT
                    "gpt2" -> GgmlTokenizerModel.GPT2
                    "rwkv" -> GgmlTokenizerModel.RWKV
                    else -> GgmlTokenizerModel.LLAMA
                }
                Model(tokenizer)
            }
            ChatTemplate.KEY -> chatTemplate = (value as? String)?.let { ChatTemplate(it) }
            Tokens.KEY -> tokens = (value as? List<*>)?.filterIsInstance<String>()?.let { Tokens(it) }
            
            // Direct token strings (if available)
            BosToken.KEY -> bosToken = (value as? String)?.let { BosToken(it) }
            EosToken.KEY -> eosToken = (value as? String)?.let { EosToken(it) }
            PaddingToken.KEY -> paddingToken = (value as? String)?.let { PaddingToken(it) }
            SeparatorToken.KEY -> separatorToken = (value as? String)?.let { SeparatorToken(it) }
            UnknownToken.KEY -> unknownToken = (value as? String)?.let { UnknownToken(it) }
            
            // Token IDs (for resolution)
            BosTokenId.KEY -> bosTokenId = (value as? Number)?.toLong()?.toUInt()?.let { BosTokenId(it) }
            EosTokenId.KEY -> eosTokenId = (value as? Number)?.toLong()?.toUInt()?.let { EosTokenId(it) }
            PaddingTokenId.KEY -> paddingTokenId = (value as? Number)?.toLong()?.toUInt()?.let { PaddingTokenId(it) }
            SeparatorTokenId.KEY -> separatorTokenId = (value as? Number)?.toLong()?.toUInt()?.let { SeparatorTokenId(it) }
            UnknownTokenId.KEY -> unknownTokenId = (value as? Number)?.toLong()?.toUInt()?.let { UnknownTokenId(it) }

            // Dynamic source fields (pattern-matched)
            else -> {
                when (value) {
                    is BaseModelAuthor -> baseModelAuthor = value
                    is BaseModelDoi -> baseModelDoi = value
                    is BaseModelName -> baseModelName = value
                    is BaseModelOrganization -> baseModelOrganization = value
                    is BaseModelRepoUrl -> baseModelRepoUrl = value
                    is BaseModelUrl -> baseModelUrl = value
                    is BaseModelUuid -> baseModelUuid = value
                    is BaseModelVersion -> baseModelVersion = value
                }
            }
        }
        return this
    }

    /**
     * Resolve token ID to actual token string using tokens array
     */
    private fun resolveToken(tokenId: UInt?, tokensList: List<String>?): String? {
        return if (tokenId != null && tokensList != null && tokenId < tokensList.size.toUInt()) {
            tokensList[tokenId.toInt()]
        } else null
    }

    /**
     * Build the LlamaModelMetadata instance, throwing an error if mandatory fields are missing
     */
    override fun build(): GgufMetadata {
        val missingFields = mutableListOf<String>()

        if (contextLength == null) missingFields.add("llama.context_length")
        if (embeddingLength == null) missingFields.add("llama.embedding_length")
        if (blockCount == null) missingFields.add("llama.block_count")
        if (feedForwardLength == null) missingFields.add("llama.feed_forward_length")
        if (ropeDimensionCount == null) missingFields.add("llama.rope.dimension_count")
        if (headCount == null) missingFields.add("llama.attention.head_count")
        if (layerNormRmsEpsilon == null) missingFields.add("llama.attention.layer_norm_rms_epsilon")

        if (missingFields.isNotEmpty()) {
            throw IllegalStateException(
                "Missing mandatory LLAMA fields: ${
                    missingFields.joinToString(
                        ", "
                    )
                }"
            )
        }

        // Resolve token IDs to actual tokens if tokens are not directly provided
        val tokensList = tokens?.value
        val resolvedBosToken = bosToken ?: resolveToken(bosTokenId?.value, tokensList)?.let { BosToken(it) }
        val resolvedEosToken = eosToken ?: resolveToken(eosTokenId?.value, tokensList)?.let { EosToken(it) }
        val resolvedPaddingToken = paddingToken ?: resolveToken(paddingTokenId?.value, tokensList)?.let { PaddingToken(it) }
        val resolvedSeparatorToken = separatorToken ?: resolveToken(separatorTokenId?.value, tokensList)?.let { SeparatorToken(it) }
        val resolvedUnknownToken = unknownToken ?: resolveToken(unknownTokenId?.value, tokensList)?.let { UnknownToken(it) }

        return LlamaModelMetadata(
            quantizationVersion = quantizationVersion!!,
            alignment = alignment ?: Alignment(32u),
            name = name,
            author = author,
            version = version,
            organization = organization,
            basename = basename,
            datasets = datasets,
            description = description,
            doi = doi,
            fileType = fileType,
            fineTune = fineTune,
            languages = languages,
            license = license,
            licenseLink = licenseLink,
            licenseName = licenseName,
            quantizedBy = quantizedBy,
            repoUrl = repoUrl,
            sizeLabel = sizeLabel,
            tags = tags,
            url = url,
            uuid = uuid,
            baseModelAuthor = baseModelAuthor,
            baseModelCount = baseModelCount,
            baseModelDoi = baseModelDoi,
            baseModelName = baseModelName,
            baseModelOrganization = baseModelOrganization,
            baseModelRepoUrl = baseModelRepoUrl,
            baseModelUrl = baseModelUrl,
            baseModelUuid = baseModelUuid,
            baseModelVersion = baseModelVersion,
            sourceDoi = sourceDoi,
            sourceRepoUrl = sourceRepoUrl,
            sourceUrl = sourceUrl,
            sourceUuid = sourceUuid,
            contextLength = contextLength!!,
            embeddingLength = embeddingLength!!,
            blockCount = blockCount!!,
            feedForwardLength = feedForwardLength!!,
            useParallelResidual = useParallelResidual,
            tensorDataLayout = tensorDataLayout,
            expertCount = expertCount,
            expertUsedCount = expertUsedCount,
            headCount = headCount!!,
            headCountKv = headCountKv,
            maxAlibiBias = maxAlibiBias,
            clampKqv = clampKqv,
            layerNormEpsilon = layerNormEpsilon,
            layerNormRmsEpsilon = layerNormRmsEpsilon!!,
            keyLength = keyLength,
            valueLength = valueLength,
            ropeDimensionCount = ropeDimensionCount!!,
            ropeFreqBase = ropeFreqBase,
            ropeScaleLinear = ropeScaleLinear,
            ropeScalingType = ropeScalingType,
            ropeScalingFactor = ropeScalingFactor,
            ropeScalingOriginalContextLength = ropeScalingOriginalContextLength,
            ropeScalingFineTuned = ropeScalingFineTuned,
            ssmConvKernel = ssmConvKernel,
            ssmInnerSize = ssmInnerSize,
            ssmStateSize = ssmStateSize,
            ssmTimeStepRank = ssmTimeStepRank,
            tokenizerModel = tokenizerModel,
            chatTemplate = chatTemplate,
            bosToken = resolvedBosToken,
            eosToken = resolvedEosToken,
            paddingToken = resolvedPaddingToken,
            separatorToken = resolvedSeparatorToken,
            unknownToken = resolvedUnknownToken
        )
    }

    companion object {
        /**
         * Create a new builder instance
         */
        fun create(): LlamaModelMetadataBuilder = LlamaModelMetadataBuilder()
    }
}