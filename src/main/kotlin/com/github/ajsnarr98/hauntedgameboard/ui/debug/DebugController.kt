package com.github.ajsnarr98.hauntedgameboard.ui.debug

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.github.ajsnarr98.hauntedgameboard.hardware.HardwareResourceManager
import com.github.ajsnarr98.hauntedgameboard.ui.ScreenController
import com.github.ajsnarr98.hauntedgameboard.util.DispatcherProvider
import com.github.ajsnarr98.hauntedgameboard.util.toImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DebugController(
    override val controllerScope: CoroutineScope,
    val resourceManager: HardwareResourceManager,
    val dispatcherProvider: DispatcherProvider,
) : ScreenController() {

    /**
     * These are defined in the order they will be shown
     */
    val defaultTabModels: Array<DebugTabModel> = arrayOf(
        DebugTabModel.Camera(
            captureButtonText = "Capture Image",
            image = null,
            isLoading = false,
        ),
    )

    var currentTabPos: Int by mutableStateOf(0)
    var currentTab: DebugTabModel by mutableStateOf(defaultTabModels[currentTabPos])

    fun onTabSelected(tabPos: Int) {
        if (tabPos == currentTabPos) return

        currentTabPos = tabPos
        currentTab = defaultTabModels[tabPos]
    }

    fun onTakePicture(model: DebugTabModel.Camera) {
        currentTab = model.copy(isLoading = true)
        controllerScope.launch(dispatcherProvider.io()) {
            val image = resourceManager.camera.takePicture().toImageBitmap()
            withContext(dispatcherProvider.main()) {
                val tab = currentTab
                if (tab is DebugTabModel.Camera) {
                    currentTab = tab.copy(image = image)
                }
            }
        }
    }
}