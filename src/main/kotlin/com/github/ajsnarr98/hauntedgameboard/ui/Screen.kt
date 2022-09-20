package com.github.ajsnarr98.hauntedgameboard.ui

import androidx.compose.runtime.Composable

abstract class Screen<T : ScreenController>(val controller: T, val screenManager: ScreenManager) {
    @Composable
    abstract fun compose()
}
