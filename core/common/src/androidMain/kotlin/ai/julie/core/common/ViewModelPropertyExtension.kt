package ai.julie.core.common

import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass

object ViewModelPropertyExtension {
    private var pendingViewModelClass: KClass<out ViewModel>? = null
    private val pendingProperties: MutableMap<String, AutoCloseable> = mutableMapOf()

    fun prepareProperty(viewModelClass: KClass<out ViewModel>, key: String, value: AutoCloseable) {
        if (pendingViewModelClass != null) {
            require(pendingViewModelClass == viewModelClass) {
                "Cannot prepare properties for different ViewModels at the same time."
            }
        }

        pendingViewModelClass = viewModelClass
        pendingProperties[key] = value
    }

    fun applyProperties(viewModel: ViewModel) {
        if (viewModel::class != pendingViewModelClass) return

        pendingProperties.forEach { (key, value) ->
            viewModel.addCloseable(key, value)
        }
        clearPendingProperties()
    }

    fun clearPendingProperties() {
        pendingProperties.clear()
        pendingViewModelClass = null
    }
}