package com.github.ajsnarr98.hauntedgameboard.hardware.camera

import com.github.ajsnarr98.hauntedgameboard.util.Initializable
import org.opencv.core.Mat
import java.io.Closeable

interface Camera : Closeable, Initializable {
    suspend fun takePicture(): Mat
}