package com.github.ajsnarr98.hauntedgameboard.games

import com.github.ajsnarr98.hauntedgameboard.ui.error.ErrorModel
import com.github.ajsnarr98.hauntedgameboard.util.Initializable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class AbstractGameManager<G : GameState, H : HardwareBoardState, S : GameStateMapper.SuccessLevel> :
    Initializable {

    private val errorChannel = Channel<ErrorModel>(Channel.BUFFERED)

    /**
     * Buffered flow of errors.
     */
    val errorFlow: Flow<ErrorModel> = errorChannel.receiveAsFlow()

    lateinit var currentGameState: G
        protected set

    lateinit var currentBoardState: H
        protected set

    abstract val gameStateMapper: GameStateMapper<G, H, S>

    /**
     * Timeout in ms. -1 means infinite.
     */
    open val READ_TIMEOUT = GameStateMapper.DEFAULT_BOARD_READER_TIMEOUT

    /**
     * Timeout in ms. -1 means infinite.
     */
    open val WRITE_TIMEOUT = GameStateMapper.DEFAULT_WRITE_TIMEOUT

    /**
     * Get the first board state and check if it is valid.
     */
    override suspend fun initialize(): Boolean {
        val initialRead = gameStateMapper.isValidBoard(timeout = READ_TIMEOUT) ?: return false
        val (gState, bState, successLvl) = initialRead
        return handleReadWithSuccessLevel(gState, bState, successLvl)
    }

    /**
     * Blocking call that manages the game.
     */
    suspend fun run() {
        TODO()
    }

    /**
     *  Return true if this sets the current state, false if not.
     */
    abstract fun handleReadWithSuccessLevel(
        gameState: G,
        hardwareBoardState: H,
        successLvl: S
    ): Boolean

    protected fun handleError(error: ErrorModel) {
        errorChannel.trySend(error)
    }
}