package com.ajsnarr.hardware

/**
 * Denotes any class that uses GPIO.
 */
interface GPIOUser {
    fun onShutdown()
}