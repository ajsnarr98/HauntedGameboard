package com.ajsnarr.hauntedgameboard.vision.chessboard.checkers

import com.ajsnarr.hauntedgameboard.games.GameState
import com.ajsnarr.hauntedgameboard.util.Color

const val BOARD_SIZE = 10

data class CheckersHardwareBoardState(
    val board: List<List<SquareState>> = Array(BOARD_SIZE) {
        Array(BOARD_SIZE) { SquareState.EMPTY }.toList()
    }.toList(),
    val p1ApproximateColor: Color,
    val p2ApproximateColor: Color,
) : GameState {
    enum class SquareState {
        P1,
        P2,
        EMPTY,
    }
}
