package com.github.ajsnarr98.hauntedgameboard.ui.debug

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowState
import com.github.ajsnarr98.hauntedgameboard.ui.ComposeScreen
import com.github.ajsnarr98.hauntedgameboard.ui.ComposeScreenManager
import org.opencv.imgproc.Imgproc

class DebugScreen(
    windowState: WindowState,
    controller: DebugController,
    screenManager: ComposeScreenManager,
) : ComposeScreen<DebugController>(windowState, controller, screenManager) {

    companion object {
        /**
         * Minimum percentage in range [0,1) of screen width for tabs to take up.
         */
        const val TAB_SCREEN_WIDTH_PERCENTAGE: Float = 0.25f
    }

    @Composable
    override fun compose() {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // tabs
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth(fraction = TAB_SCREEN_WIDTH_PERCENTAGE)
            ) {
                for ((i, model) in controller.defaultTabModels.withIndex()) {
                    Tab(
                        selected = controller.currentTabPos == i,
                        onClick = { controller.onTabSelected(i) },
                        modifier = Modifier.background(color = Color.LightGray.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = model.title,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(32.dp)
                        )
                        Divider()
                    }
                }
            }
            Divider(modifier = Modifier.width(1.dp).fillMaxHeight())
            Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1.0f).fillMaxHeight()) {
                when (val model = controller.currentTab) {
                    is DebugTabModel.Camera -> CameraDebugScreen(model)
                }
            }
        }
    }

    @Composable
    fun CameraDebugScreen(model: DebugTabModel.Camera) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxHeight(),
        ) {
            Button(onClick = { controller.onTakePicture(model) }) {
                Text(model.captureButtonText)
            }
            if (model.image != null) {
                Image(
                    bitmap = model.image,
                    contentDescription = "Taken picture"
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(100.dp).background(Color.Gray)
                ) {
                    if (model.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(textAlign = TextAlign.Center, text = "Image placeholder")
                    }
                }
            }
        }
    }
}