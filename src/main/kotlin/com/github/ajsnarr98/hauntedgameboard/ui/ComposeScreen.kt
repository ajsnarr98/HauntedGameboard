package com.github.ajsnarr98.hauntedgameboard.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.WindowState

abstract class ComposeScreen<T : ScreenController>(
    val windowState: WindowState,
    override val controller: T,
    override val screenManager: ComposeScreenManager,
) : Screen<T> {
    @Composable
    abstract fun compose()
}