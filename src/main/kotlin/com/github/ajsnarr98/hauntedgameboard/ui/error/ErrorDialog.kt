package com.github.ajsnarr98.hauntedgameboard.ui.error

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Dialog
import com.github.ajsnarr98.hauntedgameboard.ui.ScreenController
import com.github.ajsnarr98.hauntedgameboard.ui.res.Images
import com.github.ajsnarr98.hauntedgameboard.ui.res.Strings

@Composable
fun ErrorDialog(
    controller: ScreenController,
) {
    val errorModel = controller.error ?: return
    val onClose: () -> Unit = remember(errorModel) {
        {
            if (errorModel.isFatal) {

            } else {

            }
        }
    }
    Dialog(
        title = Strings.ERROR_WINDOW_TITLE,
        icon = Images.ERROR(),
        onCloseRequest = { onClose() },
    ) {

    }
}
