package ai.julie.feature.modelconfig.domain.ggufreader

import ai.julie.feature.modelconfig.domain.gguf.GgufMetadata
import ai.julie.feature.modelconfig.domain.gguf.GgufMetadataBuilderFactory
import ai.julie.feature.modelconfig.domain.gguf.general.Architecture
import ai.julie.logging.Logger
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.readByteArray
import kotlinx.io.readDoubleLe
import kotlinx.io.readFloatLe
import kotlinx.io.readIntLe
import kotlinx.io.readLongLe
import kotlinx.io.readShortLe
import kotlinx.io.readString

class GgufReader : ReadGgufMetadata {
    private val TAG = "GgufReader"

    override suspend fun readGgufMetadata(file: PlatformFile): GgufMetadata {
        require(file.exists()) { "File does not exist: ${file.path}" }

        return withContext(Dispatchers.Default) {
            // First pass: Read all metadata to find architecture
            val metadataMap = mutableMapOf<String, Any>()
            val source = file.source().buffered()
            
            source.use { bufferedSource ->
                // Read header (24 bytes total)
                val ggufMagicNumber = bufferedSource.readString(4)
                require(ggufMagicNumber == GGUF_MAGIC) { "Invalid GGUF magic number: $ggufMagicNumber" }

                val version = bufferedSource.readIntLe()
                val tensorCount = bufferedSource.readLongLe()
                val metadataCount = bufferedSource.readLongLe()

                // Read all metadata entries into map
                repeat(times = metadataCount.toInt()) {
                    // Read key length (8 bytes)
                    val keyLength = bufferedSource.readLongLe()

                    // Read key string
                    val key = bufferedSource.readString(keyLength)

                    // Read value type (4 bytes)
                    val valueType = bufferedSource.readIntLe()

                    // Read and parse the value
                    val (value, _) = readValueSequential(bufferedSource, valueType)

                    metadataMap[key] = value
                    Logger.d("[$TAG] Read metadata: key='$key', value='$value' (${value::class.simpleName})")
                }
            }

            // Create appropriate builder based on architecture
            val architectureValue = metadataMap[Architecture.KEY] as? String
            val builder = GgufMetadataBuilderFactory.createBuilder(architectureValue)
            Logger.d("[$TAG] Created builder for architecture: $architectureValue")

            // Second pass: Set all fields in builder
            metadataMap.forEach { (key, value) ->
                builder.setField(key, value)
            }

            builder.build()
        }
    }

    private fun readValueSequential(bufferedSource: Source, type: Int): Pair<Any, Long> {
        return when (type) {
            GGUF_TYPE_UINT8 -> {
                val value = (bufferedSource.readByteArray(1)[0].toInt() and 0xFF)
                value to 1L
            }

            GGUF_TYPE_INT8 -> {
                val value = bufferedSource.readByteArray(1)[0].toInt()
                value to 1L
            }

            GGUF_TYPE_UINT16 -> {
                val value = bufferedSource.readShortLe().toInt() and 0xFFFF
                value to 2L
            }

            GGUF_TYPE_INT16 -> {
                val value = bufferedSource.readShortLe().toInt()
                value to 2L
            }

            GGUF_TYPE_UINT32 -> {
                val value = bufferedSource.readIntLe().toLong() and 0xFFFFFFFFL
                value to 4L
            }

            GGUF_TYPE_INT32 -> {
                val value = bufferedSource.readIntLe()
                value to 4L
            }

            GGUF_TYPE_FLOAT32 -> {
                val value = bufferedSource.readFloatLe()
                value to 4L
            }

            GGUF_TYPE_BOOL -> {
                val value = bufferedSource.readByteArray(1)[0] != 0.toByte()
                value to 1L
            }

            GGUF_TYPE_UINT64 -> {
                val value = bufferedSource.readLongLe()
                value to 8L
            }

            GGUF_TYPE_INT64 -> {
                val value = bufferedSource.readLongLe()
                value to 8L
            }

            GGUF_TYPE_FLOAT64 -> {
                val value = bufferedSource.readDoubleLe()
                value to 8L
            }

            GGUF_TYPE_STRING -> {
                val length = bufferedSource.readLongLe()
                val value = bufferedSource.readString(length)
                value to (8 + length)
            }

            GGUF_TYPE_ARRAY -> {
                // Read array header (4 bytes type + 8 bytes length)
                val elementType = bufferedSource.readIntLe()
                val arrayLength = bufferedSource.readLongLe()

                var arrayTotalSize = 12L // header size
                val arrayValues = mutableListOf<Any>()

                when (elementType) {
                    GGUF_TYPE_STRING -> {
                        // For string arrays, read each string
                        repeat(arrayLength.toInt()) {
                            val stringLength = bufferedSource.readLongLe()
                            val stringValue = bufferedSource.readString(stringLength)
                            arrayValues.add(stringValue)
                            arrayTotalSize += 8 + stringLength
                        }
                    }

                    else -> {
                        // For fixed-size types, read each element
                        repeat(arrayLength.toInt()) {
                            val (elementValue, elementSize) = readValueSequential(
                                bufferedSource,
                                elementType
                            )
                            arrayValues.add(elementValue)
                            arrayTotalSize += elementSize
                        }
                    }
                }

                arrayValues to arrayTotalSize
            }

            else -> throw IllegalArgumentException("Unknown GGUF type: $type")
        }
    }

    companion object Companion {
        private const val GGUF_MAGIC = "GGUF"

        // GGUF data types
        private const val GGUF_TYPE_UINT8 = 0
        private const val GGUF_TYPE_INT8 = 1
        private const val GGUF_TYPE_UINT16 = 2
        private const val GGUF_TYPE_INT16 = 3
        private const val GGUF_TYPE_UINT32 = 4
        private const val GGUF_TYPE_INT32 = 5
        private const val GGUF_TYPE_FLOAT32 = 6
        private const val GGUF_TYPE_BOOL = 7
        private const val GGUF_TYPE_STRING = 8
        private const val GGUF_TYPE_ARRAY = 9
        private const val GGUF_TYPE_UINT64 = 10
        private const val GGUF_TYPE_INT64 = 11
        private const val GGUF_TYPE_FLOAT64 = 12
    }
}