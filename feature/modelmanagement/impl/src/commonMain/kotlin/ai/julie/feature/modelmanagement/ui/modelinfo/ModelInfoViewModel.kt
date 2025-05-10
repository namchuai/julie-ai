package ai.julie.feature.modelmanagement.ui.modelinfo

import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.update

class ModelInfoViewModel : ViewModel() {

    val state = viewModelState(
        savedStateBehaviour = doNotSaveState(),
        initialState = ModelInfoState(selectedTab = ModelInfoTab.MODEL_SETTINGS),
        loadTimeReporter = doNotReportLoadTime(),
    )

    fun onTabSelected(selectedTab: ModelInfoTab) {
        state.update {
            it.copy(
                selectedTab = selectedTab
            )
        }
    }
}