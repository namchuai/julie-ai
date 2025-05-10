package ai.julie.feature.message.domain.model

import com.aallam.openai.api.core.Role

@ConsistentCopyVisibility
data class EnrichedRole private constructor(
    val role: Role,
) {
    // Delegate Role properties for seamless access
    val value get() = role.role

    companion object {
        // Predefined role constants
        val System by lazy { EnrichedRole(Role.System) }
        val User by lazy { EnrichedRole(Role.User) }
        val Assistant by lazy { EnrichedRole(Role.Assistant) }
        val Function by lazy { EnrichedRole(Role.Function) }
        val Tool by lazy { EnrichedRole(Role.Tool) }
    }
}
