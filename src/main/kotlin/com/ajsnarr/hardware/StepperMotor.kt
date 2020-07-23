package com.ajsnarr.hardware

import java.lang.IllegalArgumentException
import kotlin.concurrent.thread
import kotlin.math.roundToInt

interface StepperMotor {

    /**
     * Direction to turn. Clockwise or counter clockwise.
     */
    enum class Direction { CW, CCW }

    /**
     * Steps per revolution of motor.
     */
    val DEFAULT_SPR: Int

    /**
     * Microstep resolution. Can be values like 1/2. 1/4, 1/8. Varies per
     * motor.
     */
    val RESOLUTION: Double

    /**
     * Effective steps per revolution given microstep resolution.
     */
    val SPR: Int
        get() = (DEFAULT_SPR / RESOLUTION).roundToInt()

    /**
     * Returns whether or not this motor is moving.
     * WARNING: may return true if any stepper motor is rotating.
     */
    fun isMoving(): Boolean

    /**
     * Converts given power value to a direction.
     *
     * @param power power in range [-1,1] where negative is ccw, positive is clockwise
     * @return corresponding frequency (Hz)
     */
    fun powerToDir(power: Double): Direction {
        return if (power < 0) Direction.CCW else Direction.CW
    }

    /**
     * Converts given power value to a frequency.
     *
     * @param power power in range [-1,1] where 1 is max speed, 0 is nothing
     * @return corresponding frequency (Hz)
     */
    fun powerToFreq(power: Double): Int

    /**
     * Steps given number of rotations at the given frequency.
     *
     * @param rotations number of rotations to turn
     * @param power power in range [-1,1] where 1 is max speed, 0 is nothing,
     *              and negative is ccw, positive is cw
     * @param blocking whether this is a blocking call or not
     * @return true if success, false if not
     */
    fun rotate(rotations: Double, power: Double, blocking: Boolean = false): Boolean {
        if (power < -1 || power > 1) {
            throw IllegalArgumentException("Motor power must be in range [-1,1]")
        }
        return step((rotations * SPR).toInt(), powerToDir(power), powerToFreq(power), blocking)
    }

    /**
     * Rotates at the given power until the given condition is true.
     *
     * @param condition when this condition is true, stop rotating
     * @param power power in range [-1,1] where 1 is max speed, 0 is nothing,
     *              and negative is ccw, positive is cw
     * @param blocking whether this is a blocking call or not
     * @return true if success, false if not
     */
    fun rotateUntil(condition: () -> Boolean, power: Double, blocking: Boolean = false): Boolean {
        if (power < 0 || power > 1) {
            throw IllegalArgumentException("Motor power must be in range [0,1]")
        }

        var success: Boolean
        if (blocking) {
            do {
                // step for a very high number
                success = step(
                    Short.MAX_VALUE.toInt(), powerToDir(power), powerToFreq(power),
                    blocking = false
                )
                while (isMoving() && condition()) {
                    Thread.sleep(100L) // sleep for 100 ms
                }
                // if stopped moving, but condition is still true, repeat
            } while (condition() && success)

            // if condition is now false, stop moving
            stop()
        } else {
            // step for a very high number (first outside of thread, so can get success)
            success = step(
                Short.MAX_VALUE.toInt(), powerToDir(power), powerToFreq(power), blocking = false
            )
            thread() {
                while (isMoving() && condition()) {
                    Thread.sleep(100L) // sleep for 100 ms
                }
                // if stopped moving, but condition is still true, repeat
                while (condition() && success) {
                    // step for a very high number
                    success = step(
                        Short.MAX_VALUE.toInt(), powerToDir(power), powerToFreq(power),
                        blocking = false
                    )
                    while (isMoving() && condition()) {
                        Thread.sleep(100L) // sleep for 100 ms
                    }
                }

                // if condition is now false, stop moving
                stop()
            }
        }

        return success
    }

    /**
     * Steps given number of steps at the given frequency. Generally only
     * intended for internal use.
     *
     * @param steps number of steps to turn
     * @param dir direction to turn
     * @param frequency frequency (Hz) to send steps
     * @param blocking whether this is a blocking call or not
     * @return true if success, false if not
     */
    fun step(steps: Int, dir: Direction, frequency: Int, blocking: Boolean = false): Boolean

    /**
     * Stop all rotation of this motor.
     *
     * WARNING: may stop rotation of all stepper motors
     */
    fun stop()
}