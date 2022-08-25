package com.github.ajsnarr98.hauntedgameboard.util

interface Initializable {
    /**
     * Initialize and return true if successful.
     */
    suspend fun initialize(): Boolean
}