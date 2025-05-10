package ai.julie.feature.modelmanagement.ui.modelmetadata

import ai.julie.core.common.AsyncState
import ai.julie.core.common.createAsyncLoading
import ai.julie.core.common.createAsyncSuccess
import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import ai.julie.feature.modelconfig.domain.FlowOfModelMetadata
import ai.julie.feature.modelconfig.domain.gguf.GgufMetadata
import ai.julie.feature.modelconfig.domain.gguf.LlamaModelMetadata
import ai.julie.feature.thread.domain.FlowOfActiveThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ModelMetadataViewModel(
    flowOfModelMetadata: FlowOfModelMetadata,
    flowOfActiveThread: FlowOfActiveThread,
) : ViewModel() {

    val state = viewModelState<AsyncState<ModelMetadataState>>(
        savedStateBehaviour = doNotSaveState(),
        initialState = createAsyncLoading(),
        loadTimeReporter = doNotReportLoadTime(),
    )

    private fun convertToMetadataItemList(metadata: GgufMetadata) = with(metadata) {
        buildList<MetadataItem> {
            // Core fields from GgufMetadata interface
            add(MetadataItem.PlainText(key = "Architecture", value = architecture.value.value))
            add(
                MetadataItem.PlainText(
                    key = "Quantization Version",
                    value = metadata.quantizationVersion.value.toString()
                )
            )
            add(
                MetadataItem.PlainText(
                    key = "Alignment",
                    value = metadata.alignment.value.toString()
                )
            )

            // General Information fields
            metadata.name?.let { add(MetadataItem.PlainText(key = "Model Name", value = it.value)) }
            metadata.author?.let { add(MetadataItem.PlainText(key = "Author", value = it.value)) }
            metadata.version?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Model Version",
                        value = it.value
                    )
                )
            }
            metadata.organization?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Organization",
                        value = it.value
                    )
                )
            }
            metadata.basename?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Base Name",
                        value = it.value
                    )
                )
            }
            metadata.fineTune?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Finetune",
                        value = it.value
                    )
                )
            }
            metadata.description?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Description",
                        value = it.value
                    )
                )
            }
            metadata.sizeLabel?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Size Label",
                        value = it.value
                    )
                )
            }
            metadata.license?.let { add(MetadataItem.PlainText(key = "License", value = it.value)) }
            metadata.licenseName?.let {
                add(
                    MetadataItem.PlainText(
                        key = "License Name",
                        value = it.value
                    )
                )
            }
            metadata.licenseLink?.let {
                add(
                    MetadataItem.PlainText(
                        key = "License Link",
                        value = it.value
                    )
                )
            }
            metadata.quantizedBy?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Quantized By",
                        value = it.value
                    )
                )
            }
            metadata.url?.let { add(MetadataItem.PlainText(key = "URL", value = it.value)) }
            metadata.doi?.let { add(MetadataItem.PlainText(key = "DOI", value = it.value)) }
            metadata.uuid?.let { add(MetadataItem.PlainText(key = "UUID", value = it.value)) }
            metadata.repoUrl?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Repository URL",
                        value = it.value
                    )
                )
            }
            metadata.fileType?.let {
                add(
                    MetadataItem.PlainText(
                        key = "File Type",
                        value = it.value.name
                    )
                )
            }

            // Arrays
            metadata.tags?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Tags",
                        value = it.value.joinToString(", ")
                    )
                )
            }
            metadata.languages?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Languages",
                        value = it.value.joinToString(", ")
                    )
                )
            }
            metadata.datasets?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Datasets",
                        value = it.value.joinToString(", ")
                    )
                )
            }

            // Source Information
            metadata.sourceUrl?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Source URL",
                        value = it.value
                    )
                )
            }
            metadata.sourceDoi?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Source DOI",
                        value = it.value
                    )
                )
            }
            metadata.sourceUuid?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Source UUID",
                        value = it.value
                    )
                )
            }
            metadata.sourceRepoUrl?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Source Repository URL",
                        value = it.value
                    )
                )
            }

            // Base Model Information
            metadata.baseModelCount?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Base Model Count",
                        value = it.value.toString()
                    )
                )
            }
            metadata.baseModelName?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Base Model Name",
                        value = it.value
                    )
                )
            }
            metadata.baseModelAuthor?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Base Model Author",
                        value = it.value
                    )
                )
            }
            metadata.baseModelVersion?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Base Model Version",
                        value = it.value
                    )
                )
            }
            metadata.baseModelOrganization?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Base Model Organization",
                        value = it.value
                    )
                )
            }
            metadata.baseModelUrl?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Base Model URL",
                        value = it.value
                    )
                )
            }
            metadata.baseModelDoi?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Base Model DOI",
                        value = it.value
                    )
                )
            }
            metadata.baseModelUuid?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Base Model UUID",
                        value = it.value
                    )
                )
            }
            metadata.baseModelRepoUrl?.let {
                add(
                    MetadataItem.PlainText(
                        key = "Base Model Repository URL",
                        value = it.value
                    )
                )
            }

            // If this is LlamaModelMetadata, show LLAMA-specific fields
            if (metadata is LlamaModelMetadata) {
                // LLM-specific properties
                add(
                    MetadataItem.PlainText(
                        key = "Context Length",
                        value = metadata.contextLength.value.toString()
                    )
                )
                add(
                    MetadataItem.PlainText(
                        key = "Embedding Length",
                        value = metadata.embeddingLength.value.toString()
                    )
                )
                add(
                    MetadataItem.PlainText(
                        key = "Block Count",
                        value = metadata.blockCount.value.toString()
                    )
                )
                add(
                    MetadataItem.PlainText(
                        key = "Feed Forward Length",
                        value = metadata.feedForwardLength.value.toString()
                    )
                )
                metadata.useParallelResidual?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "Use Parallel Residual",
                            value = it.value.toString()
                        )
                    )
                }
                metadata.tensorDataLayout?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "Tensor Data Layout",
                            value = it.value
                        )
                    )
                }
                metadata.expertCount?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "Expert Count",
                            value = it.value.toString()
                        )
                    )
                }
                metadata.expertUsedCount?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "Expert Used Count",
                            value = it.value.toString()
                        )
                    )
                }

                // Attention-specific properties
                add(
                    MetadataItem.PlainText(
                        key = "Head Count",
                        value = metadata.headCount.value.toString()
                    )
                )
                metadata.headCountKv?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "Key Value Head Count",
                            value = it.value.toString()
                        )
                    )
                }
                metadata.maxAlibiBias?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "Max Alibi Bias",
                            value = it.value.toString()
                        )
                    )
                }
                metadata.clampKqv?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "Clamp KQV",
                            value = it.value.toString()
                        )
                    )
                }
                metadata.layerNormEpsilon?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "Layer Norm Epsilon",
                            value = it.value.toString()
                        )
                    )
                }
                add(
                    MetadataItem.PlainText(
                        key = "Layer Norm RMS Epsilon",
                        value = metadata.layerNormRmsEpsilon.value.toString()
                    )
                )
                metadata.keyLength?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "Key Length",
                            value = it.value.toString()
                        )
                    )
                }
                metadata.valueLength?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "Value Length",
                            value = it.value.toString()
                        )
                    )
                }

                // RoPE-specific properties
                add(
                    MetadataItem.PlainText(
                        key = "RoPE Dimension Count",
                        value = metadata.ropeDimensionCount.value.toString()
                    )
                )
                metadata.ropeFreqBase?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "RoPE Frequency Base",
                            value = it.value.toString()
                        )
                    )
                }
                metadata.ropeScaleLinear?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "RoPE Scale Linear",
                            value = it.value.toString()
                        )
                    )
                }

                // RoPE Scaling properties
                metadata.ropeScalingType?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "RoPE Scaling Type",
                            value = it.value.name
                        )
                    )
                }
                metadata.ropeScalingFactor?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "RoPE Scaling Factor",
                            value = it.value.toString()
                        )
                    )
                }
                metadata.ropeScalingOriginalContextLength?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "RoPE Scaling Original Context Length",
                            value = it.value.toString()
                        )
                    )
                }
                metadata.ropeScalingFineTuned?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "RoPE Scaling Fine Tuned",
                            value = it.value.toString()
                        )
                    )
                }

                // SSM-specific properties
                metadata.ssmConvKernel?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "SSM Conv Kernel",
                            value = it.value.toString()
                        )
                    )
                }
                metadata.ssmInnerSize?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "SSM Inner Size",
                            value = it.value.toString()
                        )
                    )
                }
                metadata.ssmStateSize?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "SSM State Size",
                            value = it.value.toString()
                        )
                    )
                }
                metadata.ssmTimeStepRank?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "SSM Time Step Rank",
                            value = it.value.toString()
                        )
                    )
                }

                // Tokenizer properties
                metadata.tokenizerModel?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "Tokenizer Model",
                            value = it.value.value
                        )
                    )
                }
                metadata.chatTemplate?.let {
                    add(
                        MetadataItem.MultilineText(
                            key = "Chat Template",
                            value = it.value,
                        )
                    )
                }
                metadata.bosToken?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "BOS Token",
                            value = it.value
                        )
                    )
                }
                metadata.eosToken?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "EOS Token",
                            value = it.value
                        )
                    )
                }
                metadata.paddingToken?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "Padding Token",
                            value = it.value
                        )
                    )
                }
                metadata.separatorToken?.let {
                    add(
                        MetadataItem.PlainText(
                            key = "Separator Token",
                            value = it.value
                        )
                    )
                }
                metadata.unknownToken?.let {
                    add(MetadataItem.PlainText(key = "Unknown Token", value = it.value))
                }
            }
        }
    }

    init {
        flowOfActiveThread.flowOfActiveThread().filterNotNull().flatMapLatest {
            flowOfModelMetadata.flowOfModelMetadata(it.modelId)
        }.onEach { metadata ->
            state.update {
                createAsyncSuccess(
                    value = ModelMetadataState(
                        metadataItems = convertToMetadataItemList(metadata),
                    )
                )
            }
        }.launchIn(viewModelScope)
    }
}