package com.github.ajsnarr98.hauntedgameboard

import com.github.ajsnarr98.hauntedgameboard.hardware.camera.Camera
import com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface.RealGPIO
import com.github.ajsnarr98.hauntedgameboard.hardware.camera.RealLibCamera
import com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface.GPIOInterface
import com.github.ajsnarr98.hauntedgameboard.util.OSUtil
import cz.adamh.utils.NativeUtils
import kotlinx.coroutines.runBlocking
import org.opencv.core.Core
import java.io.IOException

@Suppress("WarningOnMainUnusedParameterMigration")
fun main(args: Array<String>) {
    println("Hello, world!")
    loadOpenCvSharedLib()

    val gpio: GPIOInterface = RealGPIO

    val isInit = runBlocking { gpio.initialize() }
//    if (!isInit) {
//        println("Failed to initialize GPIO")
//        return
//    }

    val camera: Camera = RealLibCamera()

//    PiCamera(PiCamera.Configuration().encoding(PiCamera.Encoding.JPEG)).use { camera ->
//        try {
//            camera.capture(File("output.jpg"))
//        } catch (e: CameraException) {
//            e.printStackTrace()
//        }
//    }

    runBlocking {
        println("initializing camera..")
        camera.initialize()
        println("taking picture...")
        camera.takePicture()
        println("taken")
    }

//    println("sleeping...")
//    val motor = NEMA17Stepper(dirPin = 20, stepPin = 21)
//    motor.rotate(50.0, -.5, blocking = true)
//    println("...slept...")

    camera.close()
    gpio.close()
}

fun loadOpenCvSharedLib() {
    println(Core.NATIVE_LIBRARY_NAME)
    try {
        NativeUtils.loadLibraryFromJar("/nativelib/${Core.NATIVE_LIBRARY_NAME}-${OSUtil.targetSharedLibPostfix}.${OSUtil.targetSharedLibExt}")
    } catch (e: IOException) {
        throw RuntimeException(e.toString())
    }
}
