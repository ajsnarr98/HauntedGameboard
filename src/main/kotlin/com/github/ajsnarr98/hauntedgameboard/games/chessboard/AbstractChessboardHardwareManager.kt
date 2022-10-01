package com.github.ajsnarr98.hauntedgameboard.games.chessboard

import com.github.ajsnarr98.hauntedgameboard.games.GameStateMapper
import com.github.ajsnarr98.hauntedgameboard.games.GameHardwareManager
import com.github.ajsnarr98.hauntedgameboard.games.HardwareBoardState

abstract class AbstractChessboardHardwareManager<H : HardwareBoardState, S : GameStateMapper.SuccessLevel> :
    GameHardwareManager<H, S> {
}