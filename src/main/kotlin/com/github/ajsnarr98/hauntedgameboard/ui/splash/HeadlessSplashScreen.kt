package com.github.ajsnarr98.hauntedgameboard.ui.splash

import com.github.ajsnarr98.hauntedgameboard.ui.HeadlessScreen
import com.github.ajsnarr98.hauntedgameboard.ui.HeadlessScreenManager

class HeadlessSplashScreen(
    controller: SplashController,
    screenManager: HeadlessScreenManager,
) : HeadlessScreen<SplashController>(controller, screenManager) {
    override fun draw() = write {
        if (controller.isLoading) {
            +"Loading..."
        } else {
            +"Finished Loading"
        }
    }
}