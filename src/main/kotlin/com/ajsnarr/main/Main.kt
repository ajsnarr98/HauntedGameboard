package com.ajsnarr.main

import com.ajsnarr.hardware.GPIO;
import com.ajsnarr.hardware.NEMA17Stepper

fun main(args: Array<String>) {
    println("Hello, world!")

    val isInit = GPIO.initialize();
    if (!isInit) {
        println("Failed to initialize GPIO")
        return
    }
    
    val motor = NEMA17Stepper(dirPin = 20, stepPin = 21)
    motor.rotate(50.0, -.5)

    println("sleeping...")
    Thread.sleep(10000)
    println("...slept...")

    GPIO.onShutdown();
}
