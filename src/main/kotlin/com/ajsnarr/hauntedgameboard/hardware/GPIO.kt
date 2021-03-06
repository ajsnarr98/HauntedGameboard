package com.ajsnarr.hauntedgameboard.hardware

import cz.adamh.utils.NativeUtils
import java.io.IOException

import com.ajsnarr.hauntedgameboard.util.LogLevel
import com.ajsnarr.hauntedgameboard.util.LOG_LEVEL
import java.io.Closeable

/**
 * A class containing native methods for communicating with raspberry pi gpio pins.
 */
object GPIO : Closeable {

    enum class Level(val value: Int) {
        ON(1), OFF(0), ERROR(-1);
    }

    enum class Mode(val value: Int) {
        PI_INPUT(0), PI_OUTPUT(1), PI_ALT0(4), PI_ALT1(5),
        PI_ALT2(6), PI_ALT3(7), PI_ALT4(3), PI_ALT5(2);
    }

    init {
        try {
            NativeUtils.loadLibraryFromJar("/lib/gpio.so")
        } catch (e: IOException) {
            throw RuntimeException(e.toString())
        }
        setLogLevel(LOG_LEVEL)
    }

    val users = mutableListOf<GPIOUser>()

    /**
     * Registers a user of this library to be "cleaned up" later.
     */
    fun register(user: GPIOUser) {
        users.add(user)
    }

    /**
     * Cleanup a specific user and remove it from tracked list.
     */
    fun cleanup(user: GPIOUser) {
        if (users.remove(user)) user.onShutdown()
    }

    /**
     * Cleans up all users and terminates lib.
     */
    override fun close() {
        terminate()
    }

    /**
     * Sets the log level within native code.
     */
    fun setLogLevel(logLevel: LogLevel) {
        _setLogLevel(logLevel.value)
    }

    private external fun _setLogLevel(logLevel: Int): Int

    /**
     * Initialises the pigpio library. Must call before using other functions.
     *
     * @return true if successful, false otherwise.
     */
    fun initialize(): Boolean {
        return _initialize() >= 0
    }

    private external fun _initialize(): Int

    /**
     * Cleans all users and terminates the pigpio library. Must be called
     * before quiting.
     *
     * @return true if successful, false otherwise.
     */
    fun terminate(): Boolean {
        for (user in users) {
            user.onShutdown()
        }
        return _terminate() >= 0 // finally, terminate pigpio
    }

    private external fun _terminate(): Int

    /**
     * Sets the GPIO mode, typically input or output.
     *
     * @param gpio gpio pin to set the mode for
     * @param mode mode to set
     * @return true if successful, false otherwise.
     */
    fun setMode(gpio: Int, mode: Mode): Boolean {
        validateGpioPinNum(gpio)
        return _setMode(gpio, mode.value) >= 0
    }

    private external fun _setMode(gpio: Int, mode: Int): Int

    /**
     * Gets the GPIO mode.
     *
     * @param gpio gpio pin to get mode for
     * @return mode of the given pin
     */
    fun getMode(gpio: Int): Mode {
        validateGpioPinNum(gpio)
        val modeVal = _getmode(gpio)
        for (mode in Mode.values()) {
            if (mode.value == modeVal) {
                return mode
            }
        }
        throw IllegalStateException("Unknown mode returned from native _getMode")
    }

    private external fun _getmode(gpio: Int): Int

    /**
     * Reads the GPIO level, on or off.
     *
     * @param gpio gpio pin to read
     * @return on, off, or error.
     */
    fun read(gpio: Int): Level {
        validateGpioPinNum(gpio)
        val level = _read(gpio)
        return if (level > 0) {
            Level.ON
        } else if (level == 0) {
            Level.OFF
        } else {
            Level.ERROR
        }
    }

    private external fun _read(gpio: Int): Int

    /**
     * Sets the GPIO level, on or off.
     *
     * @param gpio gpio pin to write to
     * @param level level to set. Must be ON or OFF
     * @return true if successful, false otherwise.
     */
    fun write(gpio: Int, level: Level): Boolean {
        validateGpioPinNum(gpio)
        require(level != Level.ERROR) { "Must write either ON or OFF" }
        return _write(gpio, level.value) >= 0
    }

    private external fun _write(gpio: Int, level: Int): Int

    /**
     * Clears all waveforms and any related data and stops the current waveform.
     *
     * @return true if successful, false otherwise.
     */
    fun waveClear(): Boolean {
        return _waveClear() >= 0
    }

    private external fun _waveClear(): Int

    /**
     * Generate a ramp of waveforms for specified number of steps at the given
     * frequencies.
     *
     * @param gpio gpio pin to write to
     * @param rampFrequencies array of frequencies for each set of steps (in order)
     * @param rampNSteps parallel array of steps (in order)
     * @return true if successful, false otherwise.
     */
    fun waveRamps(gpio: Int, rampFrequencies: IntArray, rampNSteps: IntArray): Boolean {
        validateGpioPinNum(gpio)
        require(rampFrequencies.size == rampNSteps.size) { "Both array args must have same length." }
        require(rampFrequencies.size != 0) { "Passed waveRamp arrays cannot be empty" }
        return _waveRamps(gpio, rampFrequencies, rampNSteps) >= 0
    }

    private external fun _waveRamps(gpio: Int, rampFrequencies: IntArray, rampNSteps: IntArray): Int

    /**
     * Return whether or not a waveform is being transmitted.
     * @return true if a waveform is being transmitted, false otherwise.
     */
    fun waveIsBusy(): Boolean {
        return _waveIsBusy()
    }

    private external fun _waveIsBusy(): Boolean

    /**
     * Makes sure gpio is valid
     * @param gpio gpio pin number
     */
    @Throws(IllegalArgumentException::class)
    private fun validateGpioPinNum(gpio: Int) {
        require(!(gpio < 0 || gpio > 27)) { "Invalid GPIO pin number: $gpio" }
    }
}