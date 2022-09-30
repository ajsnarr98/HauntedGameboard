package com.github.ajsnarr98.hauntedgameboard.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.io.ByteArrayInputStream

fun Mat.toImageBitmap(): ImageBitmap {
    val buf = MatOfByte()
    Imgcodecs.imencode(".jpg", this, buf)
    return loadImageBitmap(ByteArrayInputStream(buf.toArray()))
}
