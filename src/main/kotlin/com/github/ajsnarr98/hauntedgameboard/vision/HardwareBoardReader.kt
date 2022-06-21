package com.github.ajsnarr98.hauntedgameboard.vision

import com.github.ajsnarr98.hauntedgameboard.games.BoardReader
import com.github.ajsnarr98.hauntedgameboard.games.GameState

const val DEFAULT_HARDWARE_BOARD_READER_TIMEOUT = 500 // ms

/**
 * A class responsible for figuring out the validity of a board and resolving
 * the current [HardwareBoardState].
 */
interface HardwareBoardReader<G: GameState, H : HardwareBoardState, S: BoardReader.SuccessLevel> {

    /**
     * Reads the current board state using the last known board state.
     *
     * @param timeout (measured in ms) if the search cannot find the board,
     *                keeps retrying until timeout is up
     */
    fun resolveBoardState(
        timeout: Int = DEFAULT_HARDWARE_BOARD_READER_TIMEOUT,
        lastKnownGameState: G,
        lastSuccessLevel: S? = null
    ): Pair<H, S>

    /**
     * Detects whether this is a valid board. Board should be empty.
     *
     * @param timeout (measured in ms) if the search cannot find the board,
     *                keeps retrying until timeout is up
     *
     * @return A new hardware board state and success level if board might be correct
     *         or null if board is not matching.
     */
    fun isValidBoard(
        timeout: Int = DEFAULT_HARDWARE_BOARD_READER_TIMEOUT
    ): Pair<H, S>?
}