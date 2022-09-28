package com.github.ajsnarr98.hauntedgameboard.ui.debug

import androidx.compose.ui.graphics.ImageBitmap


sealed interface DebugTabModel {
    val title: String

    data class Camera(
        val captureButtonText: String,
        val image: ImageBitmap?,
        val isLoading: Boolean,
    ) : DebugTabModel {
        override val title: String = "Camera"
    }
}