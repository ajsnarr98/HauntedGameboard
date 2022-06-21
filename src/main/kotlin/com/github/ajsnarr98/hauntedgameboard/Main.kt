package com.github.ajsnarr98.hauntedgameboard

import com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface.RealGPIO
import com.github.ajsnarr98.hauntedgameboard.hardware.camera.PiCamera
import com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface.GPIOInterface
import uk.co.caprica.picam.CameraException
import java.io.File

@Suppress("WarningOnMainUnusedParameterMigration")
fun main(args: Array<String>) {
    println("Hello, world!")

    val gpio: GPIOInterface = RealGPIO

    val isInit = gpio.initialize();
    if (!isInit) {
        println("Failed to initialize GPIO")
        return
    }

    println("taking picture...")
    PiCamera(PiCamera.Configuration().encoding(PiCamera.Encoding.JPEG)).use { camera ->
        try {
            camera.capture(File("output.jpg"))
        } catch (e: CameraException) {
            e.printStackTrace()
        }
    }
    println("taken..")

//    println("sleeping...")
//    val motor = NEMA17Stepper(dirPin = 20, stepPin = 21)
//    motor.rotate(50.0, -.5, blocking = true)
//    println("...slept...")

    RealGPIO.close()
}
