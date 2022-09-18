package com.github.ajsnarr98.hauntedgameboard.hardware.camera

import com.github.ajsnarr98.hauntedgameboard.util.Initializable
import org.opencv.core.Mat
import java.io.Closeable
import java.io.IOException

interface Camera : Closeable, Initializable {
    suspend fun takePicture(): Mat

    class PictureException : IOException {
        constructor() : super()
        constructor(message: String) : super(message)
        constructor(message: String, cause: Throwable) : super(message, cause)
        constructor(cause: Throwable) : super(cause)
    }
}