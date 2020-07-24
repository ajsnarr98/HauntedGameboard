package com.ajsnarr.hauntedgameboard

import com.ajsnarr.hauntedgameboard.hardware.GPIO;
import com.ajsnarr.hauntedgameboard.hardware.steppermotor.NEMA17Stepper

fun main(args: Array<String>) {
    println("Hello, world!")

    val isInit = GPIO.initialize();
    if (!isInit) {
        println("Failed to initialize GPIO")
        return
    }

    println("sleeping...")
    val motor = NEMA17Stepper(dirPin = 20, stepPin = 21)
    motor.rotate(50.0, -.5, blocking = true)
    println("...slept...")

    GPIO.onShutdown();
}
