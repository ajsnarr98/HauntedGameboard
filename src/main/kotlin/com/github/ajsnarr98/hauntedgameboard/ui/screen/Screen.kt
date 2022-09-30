package com.github.ajsnarr98.hauntedgameboard.ui.screen

import com.github.ajsnarr98.hauntedgameboard.ui.screenmanager.AbstractScreenManager
import com.github.ajsnarr98.hauntedgameboard.ui.screencontroller.ScreenController

interface Screen<T : ScreenController> {
    val controller: T
    val screenManager: AbstractScreenManager<*>
}
