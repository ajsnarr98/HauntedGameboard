package com.github.ajsnarr98.hauntedgameboard.games.chessboard.checkers

import androidx.compose.ui.graphics.Color
import com.github.ajsnarr98.hauntedgameboard.games.GameState

data class CheckersHardwareBoardState(
    val board: List<List<SquareState>> = Array(BOARD_SIZE) {
        Array(BOARD_SIZE) { SquareState.EMPTY }.toList()
    }.toList(),
    val p1ApproximateColor: Color,
    val p2ApproximateColor: Color,
) : GameState {

    companion object {
        const val BOARD_SIZE = 10
    }

    enum class SquareState {
        P1,
        P2,
        EMPTY,
    }
}
