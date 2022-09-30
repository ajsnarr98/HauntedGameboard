package com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface

import com.github.ajsnarr98.hauntedgameboard.util.LogLevel

class FakeGPIO : AbstractGPIOInterface() {
    override fun initializeConnection(): Boolean {
        return true
    }

    override fun terminateConnection(): Boolean {
        return true
    }

    override fun setLogLevel(logLevel: LogLevel) {}

    override fun setMode(gpio: Int, mode: GPIO.Mode): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMode(gpio: Int): GPIO.Mode {
        TODO("Not yet implemented")
    }

    override fun read(gpio: Int): GPIO.Level {
        TODO("Not yet implemented")
    }

    override fun write(gpio: Int, level: GPIO.Level): Boolean {
        TODO("Not yet implemented")
    }

    override fun waveClear(): Boolean {
        TODO("Not yet implemented")
    }

    override fun waveRamps(gpio: Int, rampFrequencies: IntArray, rampNSteps: IntArray): Boolean {
        TODO("Not yet implemented")
    }

    override fun waveIsBusy(): Boolean {
        TODO("Not yet implemented")
    }
}