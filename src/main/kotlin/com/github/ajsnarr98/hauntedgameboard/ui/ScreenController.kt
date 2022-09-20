package com.github.ajsnarr98.hauntedgameboard.ui

import kotlinx.coroutines.CoroutineScope

/**
 * A controller that manages the state of a screen.
 */
interface ScreenController {
    val controllerScope: CoroutineScope
}