package com.github.ajsnarr98.hauntedgameboard.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.structuralEqualityPolicy
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.awt.GraphicsEnvironment

/**
 * Anything that holds/manages mutable screen state.
 */
abstract class ScreenStateController {
    private val triggerRedrawChannel = Channel<Unit>(Channel.CONFLATED)

    /**
     * NOTE: this is only intended to be used with headless (text-only) display mode.
     */
    val triggerRedrawFlow: Flow<Unit> = triggerRedrawChannel.receiveAsFlow()

    /**
     * Force trigger a redraw.
     */
    fun invalidateScreenState() {
        triggerRedrawChannel.trySend(Unit)
    }

    protected fun <T> mutableStateOf(
        value: T,
        policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy()
    ): MutableState<T> {
        return if (isHeadless) {
            HeadlessMutableState(value, policy)
        } else {
            // default compose method
            androidx.compose.runtime.mutableStateOf(value, policy)
        }
    }

    private inner class HeadlessMutableState<T>(
        value: T,
        val policy: SnapshotMutationPolicy<T>
    ) : MutableState<T> {
        @Volatile
        private var _value: T = value

        override var value: T
            get() = _value
            set(value) {
                if (!policy.equivalent(_value, value)) {
                    _value = value
                    triggerRedrawChannel.trySend(Unit)
                }
            }

        /**
         * The componentN() operators allow state objects to be used with the property destructuring
         * syntax
         *
         * ```
         * var (foo, setFoo) = remember { mutableStateOf(0) }
         * setFoo(123) // set
         * foo == 123 // get
         * ```
         */
        override operator fun component1(): T = value

        override operator fun component2(): (T) -> Unit = { value = it }
    }

    companion object {
        val isHeadless = GraphicsEnvironment.isHeadless()
    }
}