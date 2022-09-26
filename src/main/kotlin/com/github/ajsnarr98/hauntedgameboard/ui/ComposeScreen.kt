package com.github.ajsnarr98.hauntedgameboard.ui

import androidx.compose.runtime.Composable

abstract class ComposeScreen<T : ScreenController>(
    override val controller: T,
    override val screenManager: ComposeScreenManager,
) : Screen<T> {
    @Composable
    abstract fun compose()
}