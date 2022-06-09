package com.ajsnarr.hauntedgameboard.games.checkers

import com.ajsnarr.hauntedgameboard.games.GameState
import com.ajsnarr.hauntedgameboard.util.Color

const val BOARD_SIZE = 10

data class CheckersGameState(
    val board: List<List<SquareState>> = Array(BOARD_SIZE) {
        Array(BOARD_SIZE) { SquareState.EMPTY }.toList()
    }.toList(),
    val p1ApproximateColor: Color,
    val p2ApproximateColor: Color,
) : GameState {
    enum class SquareState {
        P1_KING,
        P1_NORMAL,
        P2_KING,
        P2_NORMAL,
        EMPTY,
    }
}
