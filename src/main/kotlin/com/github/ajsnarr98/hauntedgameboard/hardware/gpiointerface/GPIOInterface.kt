package com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface

import com.github.ajsnarr98.hauntedgameboard.util.Initializable
import com.github.ajsnarr98.hauntedgameboard.util.LogLevel
import java.io.Closeable

/**
 * An interface for communicating with real or fake raspberry pi gpio pins.
 *
 * There should only ever be ONE instance of an object that implements the interface
 * at runtime.
 */
interface GPIOInterface : Closeable, Initializable {

    companion object {
        const val MIN_PIN = 0
        const val MAX_PIN = 27
    }

    val users: List<GPIO.User>

    /**
     * Registers a user of this library to be "cleaned up" later.
     */
    fun registerUser(user: GPIO.User)

    /**
     * Cleanup a specific user and remove it from tracked list.
     */
    fun cleanupUser(user: GPIO.User)

    /**
     * Cleans up all users and terminates gpio, must be called before quiting.
     */
    override fun close()

    /**
     * Sets the log level within native code.
     */
    fun setLogLevel(logLevel: LogLevel)

    /**
     * Initialises the gpio connection. Must call before using other functions.
     *
     * @return true if successful, false otherwise.
     */
    override fun initialize(): Boolean

    /**
     * Sets the GPIO mode, typically input or output.
     *
     * @param gpio gpio pin to set the mode for
     * @param mode mode to set
     * @return true if successful, false otherwise.
     */
    fun setMode(gpio: Int, mode: GPIO.Mode): Boolean

    /**
     * Gets the GPIO mode.
     *
     * @param gpio gpio pin to get mode for
     * @return mode of the given pin
     */
    fun getMode(gpio: Int): GPIO.Mode

    /**
     * Reads the GPIO level, on or off.
     *
     * @param gpio gpio pin to read
     * @return on, off, or error.
     */
    fun read(gpio: Int): GPIO.Level

    /**
     * Sets the GPIO level, on or off.
     *
     * @param gpio gpio pin to write to
     * @param level level to set. Must be ON or OFF
     * @return true if successful, false otherwise.
     */
    fun write(gpio: Int, level: GPIO.Level): Boolean

    /**
     * Clears all waveforms and any related data and stops the current waveform.
     *
     * @return true if successful, false otherwise.
     */
    fun waveClear(): Boolean

    /**
     * Generate a ramp of waveforms for specified number of steps at the given
     * frequencies.
     *
     * @param gpio gpio pin to write to
     * @param rampFrequencies array of frequencies for each set of steps (in order)
     * @param rampNSteps parallel array of steps (in order)
     * @return true if successful, false otherwise.
     */
    fun waveRamps(gpio: Int, rampFrequencies: IntArray, rampNSteps: IntArray): Boolean

    /**
     * Return whether or not a waveform is being transmitted.
     * @return true if a waveform is being transmitted, false otherwise.
     */
    fun waveIsBusy(): Boolean
}