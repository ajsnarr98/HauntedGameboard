package com.ajsnarr.hauntedgameboard.hardware.rails

/**
 * Linear rail system that moves something along a range of x-y coordinates.
 */
interface LinearRail2D {
    var MIN_X: Double
    var MIN_Y: Double
    var MAX_X: Double
    var MAX_Y: Double

    var x: Double
    var y: Double

    /**
     * Calibrates this rail system.
     */
    fun calibrate() {}

    /**
     * Positions rails to the given x,y pos.
     *
     * @param x dest x
     * @param y dest y
     * @param power power in range [-1,1]
     */
    fun goToPos(targetX: Double, targetY: Double, power: Double)

    /**
     * Goes from any (even unknown pos) to a starting position.
     */
    fun resetPos()
}
