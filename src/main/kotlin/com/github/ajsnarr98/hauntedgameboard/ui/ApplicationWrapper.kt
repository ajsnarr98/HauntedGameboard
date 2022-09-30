package com.github.ajsnarr98.hauntedgameboard.ui

import com.github.ajsnarr98.hauntedgameboard.hardware.HardwareResourceManager
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.CoroutineContext

/**
 * @property exitApplication Only exits the application, no resource closing.
 */
sealed class ApplicationWrapper protected constructor(
    private val mainContext: CoroutineContext,
    private val hardwareResourceManager: HardwareResourceManager,
    private val exitApplication: () -> Unit,
) {
    /**
     * @param stopApplicationHere Actually exit the application in this call if true, else
     *                            only prepare to exit.
     */
    fun closeApplication(stopApplicationHere: Boolean = true) {
        try {
            com.github.ajsnarr98.hauntedgameboard.onRequestCloseApplication(
                mainContext,
                hardwareResourceManager
            )
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            if (stopApplicationHere) {
                exitApplication()
            }
        }
    }

    /**
     * Warning, this may throw
     */
    private fun onRequestCloseApplication() {
        runBlocking {
            withTimeout(10000L) {
                mainContext.cancel()
                hardwareResourceManager.close()
            }
        }
    }
}

