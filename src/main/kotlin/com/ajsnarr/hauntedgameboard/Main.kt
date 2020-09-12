package com.ajsnarr.hauntedgameboard

import com.ajsnarr.hauntedgameboard.hardware.GPIO
import com.ajsnarr.hauntedgameboard.hardware.camera.PiCamera
import uk.co.caprica.picam.CameraException
import java.io.File


fun main(args: Array<String>) {
    println("Hello, world!")

    val isInit = GPIO.initialize();
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

    GPIO.close()
}
