package com.github.ajsnarr98.hauntedgameboard.ui

import androidx.compose.ui.window.ApplicationScope
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
            onRequestCloseApplication()
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

class ComposeApplicationWrapper(
    mainContext: CoroutineContext,
    hardwareResourceManager: HardwareResourceManager,
    applicationScope: ApplicationScope,
) : ApplicationWrapper(mainContext, hardwareResourceManager, exitApplication = {
    applicationScope.exitApplication()
})

class HeadlessApplicationWrapper(
    mainContext: CoroutineContext,
    hardwareResourceManager: HardwareResourceManager,
) : ApplicationWrapper(mainContext, hardwareResourceManager, exitApplication = {
    throw Exception("Exiting application...")
})

