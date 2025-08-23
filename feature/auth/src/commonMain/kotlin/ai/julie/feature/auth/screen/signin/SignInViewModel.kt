package ai.julie.feature.auth.screen.signin

import ai.julie.core.common.createAsyncLoading
import ai.julie.core.common.createAsyncSuccess
import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.update

class SignInViewModel : ViewModel() {

    val state = viewModelState(
        savedStateBehaviour = doNotSaveState(),
        initialState = SignInState(),
        loadTimeReporter = doNotReportLoadTime(),
    )

    fun onEmailChange(email: String) {
        state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        state.update { it.copy(password = password) }
    }

    fun onSignInClick() {
        state.update { it.copy(signingInState = createAsyncLoading()) }
        // Simulate sign-in process
        // In a real application, you would call a use case or repository method here
        state.update { it.copy(signingInState = createAsyncSuccess(Unit)) }
    }
}