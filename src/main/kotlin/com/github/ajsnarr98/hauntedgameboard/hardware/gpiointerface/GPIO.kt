package com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface

/**
 * Small classes and constants associated with GPIO.
 */
object GPIO {

    enum class Level(val value: Int) {
        ON(1), OFF(0), ERROR(-1);
    }

    enum class Mode(val value: Int) {
        PI_INPUT(0), PI_OUTPUT(1), PI_ALT0(4), PI_ALT1(5),
        PI_ALT2(6), PI_ALT3(7), PI_ALT4(3), PI_ALT5(2);
    }

    /**
     * Denotes any class that uses GPIO.
     */
    interface User {
        fun onShutdown()
    }
}