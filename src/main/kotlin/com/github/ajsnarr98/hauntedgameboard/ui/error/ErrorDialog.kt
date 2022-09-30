package com.github.ajsnarr98.hauntedgameboard.ui.error

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import com.github.ajsnarr98.hauntedgameboard.ui.ApplicationWrapper
import com.github.ajsnarr98.hauntedgameboard.ui.res.Colors
import com.github.ajsnarr98.hauntedgameboard.ui.res.Dimens
import com.github.ajsnarr98.hauntedgameboard.ui.screencontroller.ScreenController
import com.github.ajsnarr98.hauntedgameboard.ui.res.Images
import com.github.ajsnarr98.hauntedgameboard.ui.res.Strings

@Composable
fun ErrorDialog(
    controller: ScreenController,
    applicationWrapper: ApplicationWrapper,
) {
    val errorModel = controller.error ?: return
    val onClose: () -> Unit = remember(errorModel) {
        {
            if (errorModel.isFatal) {
                applicationWrapper.closeApplication()
            } else {
                controller.dismissError()
            }
        }
    }
    Dialog(
        title = Strings.ERROR_WINDOW_TITLE,
        icon = Images.ERROR(),
        state = rememberDialogState(size = DpSize(700.dp, 500.dp)),
        onCloseRequest = { onClose() },
    ) {
        Card(
            border = BorderStroke(1.dp, Colors.ERROR_TEXT_COLOR),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Image(
                    painter = Images.ERROR(),
                    contentDescription = null,
                )
                Text(
                    text = errorModel.userFacingMessage,
                    fontSize = Dimens.ERROR_TEXT_SIZE,
                    color = Colors.ERROR_TEXT_COLOR,
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 30.dp)
                )
                Spacer(Modifier.height(15.dp))
                Button(onClick = onClose) {
                    Text(
                        text = if (errorModel.isFatal) Strings.FATAL_ERROR_DISMISS else Strings.NON_FATAL_ERROR_DISMISS,
                        fontSize = Dimens.ERROR_TEXT_SIZE,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(Modifier.height(15.dp))
            }
        }
    }
}
