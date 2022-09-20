package com.github.ajsnarr98.hauntedgameboard.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.LinkedList

/**
 * Manages the screen stack and displays the current screen.
 */
class ScreenManager() {
    private val stack: LinkedList<Screen<out ScreenController>> = LinkedList()

    private var currentScreen: Screen<out ScreenController>? by mutableStateOf(stack.peek())

    private fun updateCurrentScreen() {
        currentScreen = stack.peek()
    }

    /**
     * Push given screen onto stack and immediately after popping current
     * screen off of stack.
     */
    fun pushAndReplace(screen: Screen<out ScreenController>) {
        stack.pop()
        stack.push(screen)
        updateCurrentScreen()
    }

    /**
     * Push given screen onto stack.
     */
    fun push(screen: Screen<out ScreenController>) {
        stack.push(screen)
        updateCurrentScreen()
    }

    /**
     * Pop current screen off of stack. Operation will fail if stack only has
     * on screen remaining.
     *
     * @return true if success else false
     */
    fun pop(): Boolean {
        return if (stack.size > 1) {
            stack.pop()
            updateCurrentScreen()
            true
        } else {
            // TODO log error
            false
        }
    }

    /**
     * Make sure to push a screen before calling this the first time.
     */
    @Composable
    fun compose() {
        currentScreen?.compose() ?: throw IllegalStateException("No screen in stack to draw")
    }
}