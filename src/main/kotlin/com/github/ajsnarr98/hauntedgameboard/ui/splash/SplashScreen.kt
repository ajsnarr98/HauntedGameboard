package com.github.ajsnarr98.hauntedgameboard.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.WindowState
import com.github.ajsnarr98.hauntedgameboard.ui.ComposeScreen
import com.github.ajsnarr98.hauntedgameboard.ui.ComposeScreenManager
import com.github.ajsnarr98.hauntedgameboard.ui.debug.DebugController
import com.github.ajsnarr98.hauntedgameboard.ui.debug.DebugScreen

class SplashScreen(
    windowState: WindowState,
    controller: SplashController,
    screenManager: ComposeScreenManager,
) : ComposeScreen<SplashController>(windowState, controller, screenManager) {
    @Composable
    override fun compose() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (controller.isLoading) {
                CircularProgressIndicator()
            } else {
                screenManager.push(
                    DebugScreen(
                        windowState = windowState,
                        controller = DebugController(
                            controllerScope = controller.controllerScope,
                            resourceManager = controller.resourceManager,
                            dispatcherProvider = controller.dispatcherProvider,
                        ),
                        screenManager = screenManager,
                    )
                )
            }
        }
    }
}
