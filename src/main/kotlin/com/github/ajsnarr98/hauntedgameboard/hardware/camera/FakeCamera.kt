package com.github.ajsnarr98.hauntedgameboard.hardware.camera

import org.opencv.core.Mat

class FakeCamera : Camera {
    override suspend fun takePicture(): Mat {
        TODO("Not yet implemented")
    }

    override fun close() {}

    override suspend fun initialize(): Boolean {
        return true
    }
}