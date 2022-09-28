package com.github.ajsnarr98.hauntedgameboard.ui.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.github.ajsnarr98.hauntedgameboard.hardware.HardwareResourceManager
import com.github.ajsnarr98.hauntedgameboard.ui.ScreenController
import com.github.ajsnarr98.hauntedgameboard.util.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Controller responsible for initial app loading.
 */
class SplashController(
    override val controllerScope: CoroutineScope,
    val resourceManager: HardwareResourceManager,
    val dispatcherProvider: DispatcherProvider,
) : ScreenController() {

    var isLoading: Boolean by mutableStateOf(true)

    init {
        controllerScope.launch(dispatcherProvider.io()) {
            try {
                resourceManager.initialize()
                withContext(dispatcherProvider.main()) { isLoading = false }
            } catch (e: HardwareResourceManager.HardwareInitializationException) {
                // TODO show error dialog
            }
        }
    }
}