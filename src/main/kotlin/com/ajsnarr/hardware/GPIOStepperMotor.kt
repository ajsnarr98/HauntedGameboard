package com.ajsnarr.hardware

private val CW = GPIO.Level.ON
private val CCW = GPIO.Level.OFF

/**
 * A stepper motor controlled through the raspberry pi GPIO pins and a
 * controller board.
 *
 * @property dirPin GPIO pin for direction
 * @property stepPin GPIO pin for stepping
 */
abstract class GPIOStepperMotor(private val dirPin: Int, private val stepPin: Int) : StepperMotor, GPIOUser {

    init {
        GPIO.setMode(dirPin, GPIO.Mode.PI_OUTPUT)
        GPIO.setMode(stepPin, GPIO.Mode.PI_OUTPUT)

        @Suppress("LeakingThis")
        GPIO.register(this) // this must be last statement in constructor
    }

    override fun isMoving(): Boolean {
        // NOTE: this will return true if ANY GPIO stepper motor is moving
        //       or anything else that uses waves
        return GPIO.waveIsBusy()
    }

    override fun step(steps: Int, dir: StepperMotor.Direction, frequency: Int, blocking: Boolean): Boolean {
        val dirLvl = if (dir == StepperMotor.Direction.CW) CW else CCW

        println("stepping $steps at freq $frequency")
    
        GPIO.write(dirPin, dirLvl)
        return GPIO.waveRamps(stepPin, intArrayOf(frequency), intArrayOf(steps));
    }

    override fun stop() {
        // NOTE: this will stop ANY GPIO stepper motor or anything else that
        //       uses waves
        GPIO.waveClear()
    }

    override fun onShutdown() {
        GPIO.write(dirPin, GPIO.Level.OFF)
        GPIO.waveClear()
    }
}
