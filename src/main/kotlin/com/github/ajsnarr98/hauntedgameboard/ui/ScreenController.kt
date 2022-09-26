package com.github.ajsnarr98.hauntedgameboard.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.structuralEqualityPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.awt.GraphicsEnvironment

/**
 * A controller that manages the state of a screen.
 */
abstract class ScreenController : ScreenStateController() {
    abstract val controllerScope: CoroutineScope
}