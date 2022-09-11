package com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface

/**
 * Abstract GPIO interface that handles field checking and gpio user management.
 */
abstract class AbstractGPIOInterface : GPIOInterface {

    override val users: MutableList<GPIO.User> = mutableListOf()

    /**
     * Registers a user of this library to be "cleaned up" later.
     */
    override fun registerUser(user: GPIO.User) {
        users.add(user)
    }

    /**
     * Cleanup a specific user and remove it from tracked list.
     */
    override fun cleanupUser(user: GPIO.User) {
        if (users.remove(user)) user.onShutdown()
    }

    override fun close() {
        val tmp = ArrayList(users)
        tmp.forEach { cleanupUser(it) }
        terminateConnection()
    }

    override suspend fun initialize(): Boolean {
        return initializeConnection()
    }

    /**
     * Initializes the GPIO connection.
     *
     * @return true if successful, false otherwise.
     */
    protected abstract fun initializeConnection(): Boolean

    /**
     * Terminates the gpio connection.
     *
     * @return true if successful, false otherwise.
     */
    protected abstract fun terminateConnection(): Boolean

    /**
     * Makes sure gpio is valid
     * @param gpio gpio pin number
     */
    @Throws(IllegalArgumentException::class)
    protected fun validateGpioPinNum(gpio: Int) {
        require(!(gpio < GPIOInterface.MIN_PIN || gpio > GPIOInterface.MAX_PIN)) { "Invalid GPIO pin number: $gpio" }
    }
}