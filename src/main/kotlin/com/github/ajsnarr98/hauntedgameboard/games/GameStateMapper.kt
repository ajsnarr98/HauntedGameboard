package com.github.ajsnarr98.hauntedgameboard.games

/**
 * A higher level class responsible for figuring out the validity of a board and resolving
 * the current game state.
 */
abstract class GameStateMapper<G : GameState, H : HardwareBoardState, S : GameStateMapper.SuccessLevel>(
    protected val hardwareManager: GameHardwareManager<H, S>,
) {
    interface SuccessLevel

    companion object {
        const val DEFAULT_BOARD_READER_TIMEOUT = 1000 // ms
        const val DEFAULT_WRITE_TIMEOUT = -1 // ms
    }

    /**
     * Reads the current game state using the last known hardware board state.
     *
     * @param timeout timeout in ms. A timeout of -1 means infinite.
     */
    open suspend fun resolveGameState(
        timeout: Int = DEFAULT_BOARD_READER_TIMEOUT,
        lastKnownBoardState: H,
        lastSuccessLevel: S? = null
    ): Triple<G, H, S> {
        return hardwareManager.resolveBoardState(
            timeout, lastKnownBoardState, lastSuccessLevel,
        ).let { (boardState, successLevel) ->
            Triple(
                mapHardwareBoardStateToGameState(
                    hardwareBoardState = boardState,
                ),
                lastKnownBoardState,
                successLevel,
            )
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
    open suspend fun isValidBoard(
        timeout: Int = DEFAULT_BOARD_READER_TIMEOUT
    ): Triple<G, H, S>? {
        return hardwareManager.isValidBoard(timeout)?.let { (boardState, successLevel) ->
            Triple(
                mapHardwareBoardStateToGameState(
                    hardwareBoardState = boardState,
                ),
                boardState,
                successLevel,
            )
        }
    }

    /**
     * Attempts to change the board to the given state.
     *
     * @param timeout (measured in ms) if the search cannot find the board,
     *                keeps retrying until timeout is up
     *
     * @return A success level for getting to that state.
     */
    open suspend fun goToBoardState(
        targetGameState: G,
        lastKnownBoardState: H,
        timeout: Int = DEFAULT_WRITE_TIMEOUT,
    ): S {
        return hardwareManager.goToBoardState(
            lastKnownBoardState = lastKnownBoardState,
            targetState = mapGameStateToHardwareBoardState(targetGameState, lastKnownBoardState),
            timeout = timeout,
        )
    }

    /**
     * Convert lower-level hardware board state to a full game state.
     */
    abstract fun mapHardwareBoardStateToGameState(
        hardwareBoardState: H,
    ): G

    abstract fun mapGameStateToHardwareBoardState(
        gameStage: G,
        lastKnownBoardState: H,
    ): H
}