package com.github.ajsnarr98.hauntedgameboard.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.WindowState
import com.github.ajsnarr98.hauntedgameboard.ui.error.ErrorDialog
import com.github.ajsnarr98.hauntedgameboard.ui.screenmanager.ComposeScreenManager
import com.github.ajsnarr98.hauntedgameboard.ui.screencontroller.ScreenController

abstract class ComposeScreen<T : ScreenController>(
    val windowState: WindowState,
    override val controller: T,
    override val screenManager: ComposeScreenManager,
) : Screen<T> {

    @Composable
    fun compose() {
        _compose()
        ErrorDialog(controller, screenManager.applicationWrapper)
    }

    @Composable
    protected abstract fun _compose()
}