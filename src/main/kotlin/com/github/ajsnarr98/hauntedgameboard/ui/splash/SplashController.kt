package com.github.ajsnarr98.hauntedgameboard.ui.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.github.ajsnarr98.hauntedgameboard.hardware.HardwareResourceManager
import com.github.ajsnarr98.hauntedgameboard.ui.screencontroller.ScreenController
import com.github.ajsnarr98.hauntedgameboard.util.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

/**
 * Controller responsible for initial app loading.
 */
class SplashController(
    override val controllerScope: CoroutineScope,
    val resourceManager: HardwareResourceManager,
    override val dispatcherProvider: DispatcherProvider,
) : ScreenController() {

    var isLoading: Boolean by mutableStateOf(true)

    init {
        controllerScope.launchWithErrorCapturing(dispatcherProvider.main()) {
            resourceManager.initialize()
            withContext(dispatcherProvider.main()) { isLoading = false }
        }
    }
}