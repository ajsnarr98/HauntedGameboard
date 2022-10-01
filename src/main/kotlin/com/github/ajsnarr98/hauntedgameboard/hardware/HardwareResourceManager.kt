package com.github.ajsnarr98.hauntedgameboard.hardware

import com.github.ajsnarr98.hauntedgameboard.hardware.camera.Camera
import com.github.ajsnarr98.hauntedgameboard.hardware.camera.FakeCamera
import com.github.ajsnarr98.hauntedgameboard.hardware.camera.RealLibCamera
import com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface.FakeGPIO
import com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface.GPIOInterface
import com.github.ajsnarr98.hauntedgameboard.hardware.gpiointerface.RealGPIO
import com.github.ajsnarr98.hauntedgameboard.util.DefaultDispatcherProvider
import com.github.ajsnarr98.hauntedgameboard.util.DispatcherProvider
import com.github.ajsnarr98.hauntedgameboard.util.Initializable
import kotlinx.coroutines.withContext
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
    val dispatcherProvider: DispatcherProvider,
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
    override val camera: Camera = cameraConstructor()
    override val gpio: GPIOInterface = gpioConstructor()

    /* Native gpio must be initialized on main thread. */
    private val mustInitializeOnMainThread = hashSetOf<Initializable>(gpio)

    override suspend fun initialize(): Boolean {
        val initialized = mutableListOf<Initializable>()
        var success: Boolean
        for (res in listOf<Initializable>(camera, gpio)) {
            success = if (res in mustInitializeOnMainThread) {
                withContext(dispatcherProvider.main()) { res.initialize() }
            } else {
                res.initialize()
            }
            if (!success) {
                initialized.forEach { if (it is Closeable) it.close() }
                throw HardwareResourceManager.HardwareInitializationException(res.javaClass.name)
            }
            initialized.add(res)
        }
        return true
    }

    override fun close() {
        camera.close()
        gpio.close()
    }
}
