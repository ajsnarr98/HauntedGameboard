package com.github.ajsnarr98.hauntedgameboard.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.ajsnarr98.hauntedgameboard.ui.Screen
import com.github.ajsnarr98.hauntedgameboard.ui.ScreenManager

class SplashScreen(
    controller: SplashController,
    screenManager: ScreenManager,
) : Screen<SplashController>(controller, screenManager) {
    @Composable
    override fun compose() {
        Box {
            Text(
                text = "Hello",
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}
