package com.ajsnarr.util

/**
 * Denotes an object that needs to be cleaned up before done with use.
 */
interface Cleanable {
    fun onShutdown()
}