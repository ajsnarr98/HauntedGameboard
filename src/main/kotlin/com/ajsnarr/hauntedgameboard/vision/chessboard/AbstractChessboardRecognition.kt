package com.ajsnarr.hauntedgameboard.vision.chessboard

import com.ajsnarr.hauntedgameboard.hardware.camera.Camera
import com.ajsnarr.hauntedgameboard.vision.BoardRecognition

abstract class AbstractChessboardRecognition(override val camera: Camera) : BoardRecognition {
    override fun isValidBoard(timeout: Int): Boolean {
        TODO("Not yet implemented")
    }
}