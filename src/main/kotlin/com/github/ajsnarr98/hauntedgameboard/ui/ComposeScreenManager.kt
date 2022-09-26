package com.github.ajsnarr98.hauntedgameboard.ui

import androidx.compose.runtime.Composable

class ComposeScreenManager : AbstractScreenManager<ComposeScreen<out ScreenController>>() {
    /**
     * Make sure to push a screen before calling this the first time.
     */
    @Composable
    fun compose() {
        currentScreen?.compose() ?: throw IllegalStateException("No screen in stack to draw")
    }
}