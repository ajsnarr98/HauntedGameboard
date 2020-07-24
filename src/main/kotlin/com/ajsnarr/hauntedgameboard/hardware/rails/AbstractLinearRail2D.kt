package com.ajsnarr.hauntedgameboard.hardware.rails

import com.ajsnarr.hauntedgameboard.hardware.steppermotor.StepperMotor

/**
 * Linear rail system supported by two stepper motors.
 */
abstract class AbstractLinearRail2D : LinearRail2D {
    abstract val motX: StepperMotor
    abstract val motY: StepperMotor

    /**
     * Number of motor rotations/x coordiate
     */
    abstract val rotationsPerX: Double

    /**
     * Number of rotations/y coordiate
     */
    abstract val rotationsPerY: Double

    override fun goToPos(targetX: Double, targetY: Double, power: Double) {
        // validate given target params
        if (targetX < MIN_X || targetX > MAX_X)
            throw IllegalArgumentException("targetX must be in range [$MIN_X, $MAX_X]")
        if (targetY < MIN_Y || targetY > MAX_Y)
            throw IllegalArgumentException("targetY must be in range [$MIN_Y, $MAX_Y]")

        // calculate distance and move motors
        val xDist = targetX - x
        val yDist = targetY - y
        val xRot = xDist * rotationsPerX
        val yRot = yDist * rotationsPerY

        motX.rotate(xRot, power)
        motY.rotate(yRot, power, blocking = true)
    }
}