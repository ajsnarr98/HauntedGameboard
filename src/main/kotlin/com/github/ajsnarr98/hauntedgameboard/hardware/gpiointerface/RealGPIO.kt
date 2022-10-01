package com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface

import com.github.ajsnarr98.hauntedgameboard.hardware.NativeLibraryLoadException
import cz.adamh.utils.NativeUtils
import java.io.IOException

import com.github.ajsnarr98.hauntedgameboard.util.LogLevel
import com.github.ajsnarr98.hauntedgameboard.util.LOG_LEVEL

/**
 * A class containing native methods for communicating with raspberry pi gpio pins.
 */
object RealGPIO : AbstractGPIOInterface() {

    init {
        try {
            NativeUtils.loadLibraryFromJar("/nativelib/gpio.so")
        } catch (e: IOException) {
            throw NativeLibraryLoadException(e)
        }
        setLogLevel(LOG_LEVEL)
    }

    override fun setLogLevel(logLevel: LogLevel) {
        _setLogLevel(logLevel.value)
    }

    private external fun _setLogLevel(logLevel: Int): Int

    override fun initializeConnection(): Boolean {
        return _initialize() >= 0
    }

    private external fun _initialize(): Int

    override fun terminateConnection(): Boolean {
        return _terminate() >= 0
    }

    private external fun _terminate(): Int

    override fun setMode(gpio: Int, mode: GPIO.Mode): Boolean {
        validateGpioPinNum(gpio)
        return _setMode(gpio, mode.value) >= 0
    }

    private external fun _setMode(gpio: Int, mode: Int): Int

    override fun getMode(gpio: Int): GPIO.Mode {
        validateGpioPinNum(gpio)
        val modeVal = _getMode(gpio)
        for (mode in GPIO.Mode.values()) {
            if (mode.value == modeVal) {
                return mode
            }
        }
        throw IllegalStateException("Unknown mode returned from native _getMode: $modeVal")
    }

    private external fun _getMode(gpio: Int): Int

    override fun read(gpio: Int): GPIO.Level {
        validateGpioPinNum(gpio)
        val level = _read(gpio)
        return if (level > 0) {
            GPIO.Level.ON
        } else if (level == 0) {
            GPIO.Level.OFF
        } else {
            GPIO.Level.ERROR
        }
    }

    private external fun _read(gpio: Int): Int

    override fun write(gpio: Int, level: GPIO.Level): Boolean {
        validateGpioPinNum(gpio)
        require(level != GPIO.Level.ERROR) { "Must write either ON or OFF" }
        return _write(gpio, level.value) >= 0
    }

    private external fun _write(gpio: Int, level: Int): Int

    override fun waveClear(): Boolean {
        return _waveClear() >= 0
    }

    private external fun _waveClear(): Int

    override fun waveRamps(gpio: Int, rampFrequencies: IntArray, rampNSteps: IntArray): Boolean {
        validateGpioPinNum(gpio)
        require(rampFrequencies.size == rampNSteps.size) { "Both array args must have same length." }
        require(rampFrequencies.size != 0) { "Passed waveRamp arrays cannot be empty" }
        return _waveRamps(gpio, rampFrequencies, rampNSteps) >= 0
    }

    private external fun _waveRamps(gpio: Int, rampFrequencies: IntArray, rampNSteps: IntArray): Int

    override fun waveIsBusy(): Boolean {
        return _waveIsBusy()
    }

    private external fun _waveIsBusy(): Boolean
}