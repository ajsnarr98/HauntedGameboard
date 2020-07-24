package com.ajsnarr.hauntedgameboard.hardware.steppermotor

import com.ajsnarr.hauntedgameboard.util.closest
import kotlin.math.abs
import kotlin.math.roundToInt

class NEMA17Stepper(dirPin: Int, stepPin: Int) : GPIOStepperMotor(dirPin, stepPin) {

    /** ordered greatest to least */
    private val frequencies = intArrayOf(
        10, 20, 40, 50, 80, 100, 160, 200, 250, 320, 400, 500, 800, 1000, 1600, 2000, 4000, 8000
    )

    override val DEFAULT_SPR: Int = 200

    override val RESOLUTION: Double = 1.0

    override fun powerToFreq(power: Double): Int {
        // multiply by highest frequency to get estimate
        val estimate = (abs(power) * frequencies.last()).roundToInt()

        // find closest value using binary search
        var lo = 0
        var hi = frequencies.size - 1
        var mid = hi
        while (lo <= hi) {
            mid = (lo + hi) / 2
            if (estimate < frequencies[mid]) {
                hi = mid - 1
            } else if (estimate > frequencies[mid]) {
                lo = mid + 1
            } else {
                return frequencies[mid]
            }
        }

        // return closest value near mid
        val toCheck = mutableListOf(frequencies[mid])
        if (mid > 0) toCheck.add(frequencies[mid - 1])
        if (mid < frequencies.size - 1) toCheck.add(frequencies[mid + 1])

        val ret = estimate.closest(toCheck)
        return ret
    }
}