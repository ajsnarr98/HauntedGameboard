package com.github.ajsnarr98.hauntedgameboard.ui.splash

import com.github.ajsnarr98.hauntedgameboard.ui.screen.HeadlessScreen
import com.github.ajsnarr98.hauntedgameboard.ui.screenmanager.HeadlessScreenManager

class HeadlessSplashScreen(
    controller: SplashController,
    screenManager: HeadlessScreenManager,
) : HeadlessScreen<SplashController>(controller, screenManager) {
    override fun _draw() = write {
        if (controller.isLoading) {
            +"Loading...\n"
        } else {
            +"Finished Loading\n"
        }
    }
}