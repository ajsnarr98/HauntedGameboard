package com.github.ajsnarr98.hauntedgameboard.hardware

/**
 * Denotes any class that uses GPIO.
 */
interface GPIOUser {
    fun onShutdown()
}