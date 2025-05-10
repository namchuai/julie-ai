package ai.julie.feature.modelconfig.domain.gguf.general

enum class FileTypeValue(val value: UInt) {
    ALL_F32(0u),
    MOSTLY_F16(1u),
    MOSTLY_Q4_0(2u),
    MOSTLY_Q4_1(3u),
    MOSTLY_Q4_1_SOME_F16(4u),

    @Deprecated("Support removed")
    MOSTLY_Q4_2(5u),

    @Deprecated("Support removed")
    MOSTLY_Q4_3(6u),

    MOSTLY_Q8_0(7u),
    MOSTLY_Q5_0(8u),
    MOSTLY_Q5_1(9u),
    MOSTLY_Q2_K(10u),
    MOSTLY_Q3_K_S(11u),
    MOSTLY_Q3_K_M(12u),
    MOSTLY_Q3_K_L(13u),
    MOSTLY_Q4_K_S(14u),
    MOSTLY_Q4_K_M(15u),
    MOSTLY_Q5_K_S(16u),
    MOSTLY_Q5_K_M(17u),
    MOSTLY_Q6_K(18u)
}

data class FileType(
    /**
     * An enumerated value describing the type of the majority of the tensors in the file.
     * Optional; can be inferred from the tensor types.
     */
    override val value: FileTypeValue,
) : GeneralInfo {
    override val key: String = KEY
    
    companion object {
        const val KEY = "general.file_type"
    }
}