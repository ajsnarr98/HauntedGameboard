package com.github.ajsnarr98.hauntedgameboard.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.ajsnarr98.hauntedgameboard.ui.ComposeScreen
import com.github.ajsnarr98.hauntedgameboard.ui.ComposeScreenManager

class SplashScreen(
    controller: SplashController,
    screenManager: ComposeScreenManager,
) : ComposeScreen<SplashController>(controller, screenManager) {
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
