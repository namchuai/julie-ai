package ai.julie.feature.modelconfig.domain.ggufreader

data class RawMetadataEntry(
    val key: String,
    val type: Int,
    val offset: Long,
    val size: Long
)