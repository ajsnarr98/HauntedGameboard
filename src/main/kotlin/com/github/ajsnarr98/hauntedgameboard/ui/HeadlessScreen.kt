package com.github.ajsnarr98.hauntedgameboard.ui

import java.io.PrintStream

/**
 * A screen that works solely with text.
 */
abstract class HeadlessScreen<T : ScreenController>(
    override val controller: T,
    override val screenManager: HeadlessScreenManager,
) : Screen<T> {
    abstract fun draw()

    /**
     * Creates a scope where you can use unary plus on strings to write strings.
     *
     * For example:
     * write {
     *     +"Hello World\n"
     * }
     */
    fun write(scope: WriteScope.() -> Unit) {
        WriteScope(screenManager.out).scope()
    }

    class WriteScope(val out: PrintStream) {
        operator fun String.unaryPlus() {
            out.print(this@unaryPlus)
        }
    }
}