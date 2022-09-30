package com.github.ajsnarr98.hauntedgameboard.ui.screen

import com.github.ajsnarr98.hauntedgameboard.ui.screenmanager.HeadlessScreenManager
import com.github.ajsnarr98.hauntedgameboard.ui.screencontroller.ScreenController
import java.io.PrintStream

/**
 * A screen that works solely with text.
 */
abstract class HeadlessScreen<T : ScreenController>(
    override val controller: T,
    override val screenManager: HeadlessScreenManager,
) : Screen<T> {

    fun draw() {
        _draw()
        val err = controller.error
        if (err != null) {
            write { +"${err.userFacingMessage}\n" }
            if (err.isFatal) {
                throw err.cause ?: RuntimeException("Fatal error: $err")
            }
        }
    }

    protected abstract fun _draw()

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

    class WriteScope(private val out: PrintStream) {
        operator fun String.unaryPlus() {
            out.print(this@unaryPlus)
        }
    }
}