package com.github.ajsnarr98.hauntedgameboard.ui

interface Screen<T : ScreenController> {
    val controller: T
    val screenManager: AbstractScreenManager<*>
}
