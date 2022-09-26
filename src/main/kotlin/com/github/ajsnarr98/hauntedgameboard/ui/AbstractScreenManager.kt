package com.github.ajsnarr98.hauntedgameboard.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.util.LinkedList

/**
 * Manages the screen stack and displays the current screen.
 */
abstract class AbstractScreenManager<T : Screen<out ScreenController>> : ScreenStateController() {
    private val stack: LinkedList<T> = LinkedList()

    protected var currentScreen: T? by mutableStateOf(stack.peek())

    private fun updateCurrentScreen() {
        currentScreen = stack.peek()
    }

    /**
     * Push given screen onto stack and immediately after popping current
     * screen off of stack.
     */
    fun pushAndReplace(screen: T) {
        stack.pop()
        stack.push(screen)
        updateCurrentScreen()
    }

    /**
     * Push given screen onto stack.
     */
    fun push(screen: T) {
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
}