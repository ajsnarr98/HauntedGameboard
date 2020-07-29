package com.ajsnarr.hauntedgameboard.vision

import com.ajsnarr.hauntedgameboard.hardware.camera.Camera

const val DEFAULT_IMAGE_SEARCH_TIMEOUT = 500 // ms

/**
 * Used for processing a game board.
 */
interface BoardRecognition {
    
    val camera: Camera

    /**
     * Detects whether or not this is a valid board. Board should be empty.
     *
     * @param timeout (measured in ms) if the search cannot find the board,
     *                keeps retrying a new image until timeout is up
     */
    fun isValidBoard(timeout: Int = DEFAULT_IMAGE_SEARCH_TIMEOUT): Boolean
}