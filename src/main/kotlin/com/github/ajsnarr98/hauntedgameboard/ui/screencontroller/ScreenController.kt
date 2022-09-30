package com.github.ajsnarr98.hauntedgameboard.ui.screencontroller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.github.ajsnarr98.hauntedgameboard.ui.ScreenStateController
import com.github.ajsnarr98.hauntedgameboard.ui.error.ErrorModel
import com.github.ajsnarr98.hauntedgameboard.ui.error.GenericErrorModel
import com.github.ajsnarr98.hauntedgameboard.util.DispatcherProvider
import kotlinx.coroutines.*
import java.util.LinkedList
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A controller that manages the state of a screen.
 */
abstract class ScreenController : ScreenStateController() {
    abstract val controllerScope: CoroutineScope
    abstract val dispatcherProvider: DispatcherProvider

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

    /**
     * See [CoroutineScope.launch]. Launch a coroutine, but capture thrown
     * exceptions as [ErrorModel]s. Optionally create more specific error models
     * by passing in [errorMapping].
     */
    protected fun CoroutineScope.launchWithErrorCapturing(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        errorMapping: (error: Throwable) -> ErrorModel = { GenericErrorModel(it) },
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return launch(
            context = context,
            start = start,
        ) {
            try {
                block()
            } catch (t: Throwable) {
                withContext(dispatcherProvider.main()) { queueError(errorMapping(t)) }
            }
        }
    }
}