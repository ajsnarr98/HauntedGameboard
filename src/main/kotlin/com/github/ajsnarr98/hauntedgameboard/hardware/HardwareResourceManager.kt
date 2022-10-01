package com.github.ajsnarr98.hauntedgameboard.hardware

import com.github.ajsnarr98.hauntedgameboard.hardware.camera.Camera
import com.github.ajsnarr98.hauntedgameboard.hardware.camera.FakeCamera
import com.github.ajsnarr98.hauntedgameboard.hardware.camera.RealLibCamera
import com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface.FakeGPIO
import com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface.GPIOInterface
import com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface.RealGPIO
import com.github.ajsnarr98.hauntedgameboard.util.Initializable
import java.io.Closeable

interface HardwareResourceManager : Initializable, Closeable {
    val camera: Camera
    val gpio: GPIOInterface

    class HardwareInitializationException : RuntimeException {
        constructor() : super()
        constructor(message: String) : super(message)
        constructor(message: String, cause: Throwable) : super(message, cause)
        constructor(cause: Throwable) : super(cause)
    }
}

class DefaultHardwareResourceManager(
    cameraConstructor: () -> Camera = {
        try {
            RealLibCamera()
        } catch (t: Throwable) {
            when (if (t is ExceptionInInitializerError) t.cause else t) {
                is NativeLibraryLoadException -> FakeCamera()
                is UnsatisfiedLinkError -> {
                    // TODO log error
                    FakeCamera()
                }

                else -> throw t
            }
        }
    },
    gpioConstructor: () -> GPIOInterface = {
        try {
            RealGPIO
        } catch (t: Throwable) {
            when (if (t is ExceptionInInitializerError) t.cause else t) {
                is NativeLibraryLoadException -> FakeGPIO()
                is UnsatisfiedLinkError -> {
                    // TODO log error
                    FakeGPIO()
                }

                else -> throw t
            }
        }
    },
) : HardwareResourceManager {
    override val camera: Camera = FakeCamera() //cameraConstructor()
    override val gpio: GPIOInterface = gpioConstructor()
//    override val gpio: GPIOInterface = FakeGPIO()

    override suspend fun initialize(): Boolean {
        val initialized = mutableListOf<Initializable>()
        for (res in listOf<Initializable>(camera, gpio)) {
            println("initializing ${if (res == camera) "camera" else "gpio"}")
            if (!res.initialize()) {
                println("closing all initialized $initialized")
                initialized.forEach { if (it is Closeable) it.close() }
                println("closed")
                throw HardwareResourceManager.HardwareInitializationException(res.javaClass.name)
            }
            println("initialized ${if (res == camera) "camera" else "gpio"}")
            initialized.add(res)
        }
        return true
    }

    override fun close() {
        camera.close()
        gpio.close()
    }
}
