package ai.julie.feature.modelmanagement.ui.modelmetadata

data class ModelMetadataState(
    val metadataItems: List<MetadataItem>,
)

sealed interface MetadataItem {
    data class PlainText(
        val key: String,
        val value: String,
    ) : MetadataItem

    data class MultilineText(
        val key: String,
        val value: String,
    ) : MetadataItem
}
