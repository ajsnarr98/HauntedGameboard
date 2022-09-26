package com.github.ajsnarr98.hauntedgameboard.ui

import com.github.ajsnarr98.hauntedgameboard.util.DispatcherProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.Closeable
import java.io.OutputStream
import java.io.PrintStream
import java.io.Writer
import kotlin.coroutines.CoroutineContext

class HeadlessScreenManager(
    private val mainScope: CoroutineScope,
    private val dispatcherProvider: DispatcherProvider,
) : AbstractScreenManager<HeadlessScreen<out ScreenController>>(), Closeable {

    /**
     * Output stream.
     */
    val out = PrintStream(BufferedOutputStream(System.out))

    private var outerJob: Job? = null
    private var job: Job? = null

    /**
     * Call this to start drawing screens, and block until done. Make sure this is called
     * after the first screen has been pushed to the stack.
     */
    fun startAndBlock() {
        outerJob = mainScope.launch(dispatcherProvider.main()) {
            // collect when the currentScreen changes
            invalidateScreenState()
            triggerRedrawFlow.collect {
                job?.cancelAndJoin()
                val screen = currentScreen ?: throw IllegalStateException("No current screen found to display")
                job = launch(dispatcherProvider.main()) {
                    screen.draw()
                    out.flush()
                    screen.controller.triggerRedrawFlow.collect {
                        screen.draw()
                        out.flush()
                    }
                }
            }
        }
        runBlocking { outerJob?.join() }
    }

    override fun close() {
        outerJob?.cancel()
        job?.cancel()
        out.close()
    }
}