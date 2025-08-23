package ai.julie.feature.auth.screen.signin

import ai.julie.core.common.AsyncState
import ai.julie.core.common.createAsyncIdle

data class SignInState(
    val email: String = "",
    val password: String = "",
    val signingInState: AsyncState<Unit> = createAsyncIdle(),
)