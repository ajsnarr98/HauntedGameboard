package com.github.ajsnarr98.hauntedgameboard.ui.screenmanager

import androidx.compose.runtime.Composable
import com.github.ajsnarr98.hauntedgameboard.ui.ApplicationWrapper
import com.github.ajsnarr98.hauntedgameboard.ui.screencontroller.ScreenController
import com.github.ajsnarr98.hauntedgameboard.ui.screen.ComposeScreen

class ComposeScreenManager(
    override val applicationWrapper: ApplicationWrapper,
) : AbstractScreenManager<ComposeScreen<out ScreenController>>() {
    /**
     * Make sure to push a screen before calling this the first time.
     */
    @Composable
    fun compose() {
        currentScreen?.compose() ?: throw IllegalStateException("No screen in stack to draw")
    }
}