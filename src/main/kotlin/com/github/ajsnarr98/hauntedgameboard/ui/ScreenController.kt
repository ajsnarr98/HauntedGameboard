package com.github.ajsnarr98.hauntedgameboard.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.github.ajsnarr98.hauntedgameboard.ui.error.ErrorModel
import kotlinx.coroutines.CoroutineScope
import java.util.LinkedList

/**
 * A controller that manages the state of a screen.
 */
abstract class ScreenController : ScreenStateController() {
    abstract val controllerScope: CoroutineScope

    var error: ErrorModel? by mutableStateOf(null)
        private set

    private val errorQueue = LinkedList<ErrorModel>()

    fun queueError(error: ErrorModel) {
        if (this.error == null) {
            this.error = error
        } else {
            this.errorQueue.push(error)
        }
    }

    fun dismissError() {
        this.error = if (errorQueue.isNotEmpty()) errorQueue.pop() else null
    }
}