package com.github.ajsnarr98.hauntedgameboard.games

/**
 * A class responsible for figuring out the validity of a board, resolving
 * the current [HardwareBoardState], and making updates to the board.
 */
interface GameHardwareManager<H : HardwareBoardState, S : GameStateMapper.SuccessLevel> {

    companion object {
        const val DEFAULT_READER_TIMEOUT = 500 // ms
        const val DEFAULT_WRITE_TIMEOUT = -1 // ms
    }

    /**
     * Reads the current board state using the last known board state.
     *
     * @param timeout (measured in ms) if the search cannot find the board,
     *                keeps retrying until timeout is up
     */
    suspend fun resolveBoardState(
        timeout: Int = DEFAULT_READER_TIMEOUT,
        lastKnownBoardState: H,
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
    suspend fun isValidBoard(
        timeout: Int = DEFAULT_READER_TIMEOUT
    ): Pair<H, S>?

    /**
     * Attempts to change the board to the given state.
     *
     * @param timeout (measured in ms) if the search cannot find the board,
     *                keeps retrying until timeout is up
     *
     * @return A success level for getting to that state.
     */
    suspend fun goToBoardState(
        lastKnownBoardState: H,
        targetState: H,
        timeout: Int = DEFAULT_WRITE_TIMEOUT,
    ): S
}