package com.github.ajsnarr98.hauntedgameboard

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.ajsnarr98.hauntedgameboard.hardware.DefaultHardwareResourceManager
import com.github.ajsnarr98.hauntedgameboard.hardware.HardwareResourceManager
import com.github.ajsnarr98.hauntedgameboard.hardware.NativeLibraryLoadException
import com.github.ajsnarr98.hauntedgameboard.ui.AbstractScreenManager
import com.github.ajsnarr98.hauntedgameboard.ui.ComposeScreenManager
import com.github.ajsnarr98.hauntedgameboard.ui.HeadlessScreenManager
import com.github.ajsnarr98.hauntedgameboard.ui.splash.HeadlessSplashScreen
import com.github.ajsnarr98.hauntedgameboard.ui.splash.SplashController
import com.github.ajsnarr98.hauntedgameboard.ui.splash.SplashScreen
import com.github.ajsnarr98.hauntedgameboard.util.DefaultDispatcherProvider
import com.github.ajsnarr98.hauntedgameboard.util.DispatcherProvider
import com.github.ajsnarr98.hauntedgameboard.util.OSUtil
import cz.adamh.utils.NativeUtils
import kotlinx.coroutines.*
import org.opencv.core.Core
import java.awt.GraphicsEnvironment
import java.io.IOException
import java.nio.file.Paths
import kotlin.coroutines.CoroutineContext

@Suppress("WarningOnMainUnusedParameterMigration")
fun main(args: Array<String>) {
    loadOpenCvSharedLib()
    if (GraphicsEnvironment.isHeadless() || true) {
        headlessMain()
    } else {
        composeMain()
    }
}

fun composeMain() = application {

    val hardwareResourceManager: HardwareResourceManager =
        remember { DefaultHardwareResourceManager() }

    val mainWindowState = rememberWindowState(
        placement = WindowPlacement.Maximized,
    )
    val mainContext: CoroutineContext = remember { Dispatchers.Main + Job() }
    val dispatcherProvider: DispatcherProvider = remember { DefaultDispatcherProvider() }

    // create screen manager and initialize with first screen
    val screenManager: ComposeScreenManager = remember {
        ComposeScreenManager().also { screenManager ->
            screenManager.push(
                SplashScreen(
                    controller = SplashController(
                        controllerScope = CoroutineScope(mainContext),
                        resourceManager = hardwareResourceManager,
                        dispatcherProvider = dispatcherProvider,
                    ),
                    screenManager = screenManager,
                )
            )
        }
    }

    Window(
        onCloseRequest = {
            try {
                onRequestCloseApplication(mainContext, hardwareResourceManager)
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {
                exitApplication()
            }
        },
        state = mainWindowState,
    ) {
        MaterialTheme {
            screenManager.compose()
        }
    }

//    val isInit = runBlocking { gpio.initialize() }
////    if (!isInit) {
////        println("Failed to initialize GPIO")
////        return
////    }
//
//
//    runBlocking {
//        println("initializing camera..")
//        camera.initialize()
//        println("taking picture...")
//        val picture = camera.takePicture()
//        println("taken")
//
//
//        Imgcodecs.imwrite("tmp.jpeg", picture)
//    }
//
////    println("sleeping...")
////    val motor = NEMA17Stepper(dirPin = 20, stepPin = 21)
////    motor.rotate(50.0, -.5, blocking = true)
////    println("...slept...")
//
//    gpio.close()
//    camera.close()
}

fun headlessMain() {

    println("Displaying in headless mode...")

    val hardwareResourceManager = DefaultHardwareResourceManager()
    val mainContext: CoroutineContext = Dispatchers.Main + Job()
    val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()

    // create screen manager and initialize with first screen
    val screenManager: HeadlessScreenManager = HeadlessScreenManager(
        mainScope = CoroutineScope(mainContext),
        dispatcherProvider = dispatcherProvider,
    ).also { screenManager ->
        screenManager.push(
            HeadlessSplashScreen(
                controller = SplashController(
                    controllerScope = CoroutineScope(mainContext),
                    resourceManager = hardwareResourceManager,
                    dispatcherProvider = dispatcherProvider,
                ),
                screenManager = screenManager,
            )
        )
    }

    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            try {
                println("Shutdown hook cleaning up...")
                screenManager.close()
                onRequestCloseApplication(mainContext, hardwareResourceManager)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    })
    screenManager.startAndBlock()
    println("Exiting headless mode...")
}

/**
 * Warning, this may throw
 */
fun onRequestCloseApplication(
    mainContext: CoroutineContext,
    hardwareResourceManager: HardwareResourceManager
) {
    runBlocking {
        withTimeout(10000L) {
            mainContext.cancel()
            hardwareResourceManager.close()
        }
    }
}

private fun loadOpenCvSharedLib() {
    val filename =
        "${Core.NATIVE_LIBRARY_NAME}-${OSUtil.targetSharedLibPostfix}.${OSUtil.targetSharedLibExt}"
    try {
        NativeUtils.loadLibraryFromJar("/nativelib/$filename")
    } catch (ioE: IOException) {
        // we do this extra check because intellij is dumb and not running the jar that gradle builds
        // TODO log that we did extra check
        try {
            System.load(Paths.get(System.getProperty("user.dir"), "nativelib", filename).toString())
        } catch (ulE: UnsatisfiedLinkError) {
            throw NativeLibraryLoadException(ulE)
        }
    }
}
