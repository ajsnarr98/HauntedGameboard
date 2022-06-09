package com.ajsnarr.hauntedgameboard.games

import com.ajsnarr.hauntedgameboard.vision.HardwareBoardReader
import com.ajsnarr.hauntedgameboard.vision.HardwareBoardState

const val DEFAULT_BOARD_READER_TIMEOUT = 500 // ms

/**
 * A class responsible for figuring out the validity of a board and resolving
 * the current game state.
 */
abstract class BoardReader<G : GameState, H : HardwareBoardState, S : BoardReader.SuccessLevel>(
    protected val hardwareBoardReader: HardwareBoardReader<G, H, S>,
) {
    interface SuccessLevel

    /**
     * Reads the current board state using the last known game state.
     *
     * @param timeout timeout in ms. A timeout of -1 means infinite.
     */
    open fun resolveBoardState(
        timeout: Int = DEFAULT_BOARD_READER_TIMEOUT,
        lastKnownGameState: G,
        lastSuccessLevel: S? = null
    ): Pair<G, S> {
        return hardwareBoardReader.resolveBoardState(
            timeout, lastKnownGameState, lastSuccessLevel,
        ).let { (boardState, successLevel) ->
            mapHardwareBoardStateToGameState(
                previousGameState = null,
                hardwareBoardState = boardState,
            ) to successLevel
        }
    }

    /**
     * Detects whether this is a valid board. Board should be empty.
     *
     * @param timeout (measured in ms) if the search cannot find the board,
     *                keeps retrying until timeout is up
     *
     * @return A new game state and success level if board might be correct
     *         or null if board is not matching.
     */
    open fun isValidBoard(
        timeout: Int = DEFAULT_BOARD_READER_TIMEOUT
    ): Pair<G, S>? {
        return hardwareBoardReader.isValidBoard(timeout)?.let { (boardState, successLevel) ->
            mapHardwareBoardStateToGameState(
                previousGameState = null,
                hardwareBoardState = boardState,
            ) to successLevel
        }
    }

    /**
     * Convert lower-level hardware board state to a full game state using
     * previous game state and known game rules.
     */
    abstract fun mapHardwareBoardStateToGameState(
        previousGameState: G?,
        hardwareBoardState: H,
    ): G
}