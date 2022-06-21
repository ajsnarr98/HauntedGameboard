package com.github.ajsnarr98.hauntedgameboard.hardware.rails

import com.github.ajsnarr98.hauntedgameboard.hardware.steppermotor.NEMA17Stepper
import com.github.ajsnarr98.hauntedgameboard.hardware.steppermotor.StepperMotor

/**
 * 2D Rail system using two NEMA 17 Stepper motors.
 *
 * @param dirPinX gpio pin for x motor direction
 * @param stepPinX gpio pin for x motor step
 * @param dirPinY gpio pin for y motor direction
 * @param stepPinY gpio pin for y motor step
 */
class NEMA17Rails(dirPinX: Int, stepPinX: Int, dirPinY: Int, stepPinY: Int) : AbstractLinearRail2D() {

    override val motX: StepperMotor = NEMA17Stepper(dirPin = dirPinX, stepPin = stepPinX)
    override val motY: StepperMotor = NEMA17Stepper(dirPin = dirPinY, stepPin = stepPinY)

    override val rotationsPerX: Double
        get() = TODO("Not yet implemented")
    override val rotationsPerY: Double
        get() = TODO("Not yet implemented")
    override var MIN_X: Double
        get() = TODO("Not yet implemented")
        set(value) {}
    override var MIN_Y: Double
        get() = TODO("Not yet implemented")
        set(value) {}
    override var MAX_X: Double
        get() = TODO("Not yet implemented")
        set(value) {}
    override var MAX_Y: Double
        get() = TODO("Not yet implemented")
        set(value) {}
    override var x: Double
        get() = TODO("Not yet implemented")
        set(value) {}
    override var y: Double
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun resetPos() {
        TODO("Not yet implemented")
    }
}