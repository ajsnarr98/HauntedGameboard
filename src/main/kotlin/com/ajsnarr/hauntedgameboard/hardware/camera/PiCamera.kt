package com.ajsnarr.hauntedgameboard.hardware.camera

import uk.co.caprica.picam.CameraConfiguration
import uk.co.caprica.picam.PicamNativeLibrary.installTempLibrary

open class PiCamera(config: Configuration) : Camera {

    /**
     * Wraps a caprica.picam CameraConfiguration.
     *
     * There are more functions available than wrapped here.
     */
    class Configuration() {
        private val config = CameraConfiguration.cameraConfiguration()

        /**
         * This method should only be called by the PiCamera class.
         */
        internal fun build() = uk.co.caprica.picam.Camera(config)
    }

    companion object {
        // static initializer
        init {
            // Extract the bundled picam native library to a temporary file and load it
            installTempLibrary();
        }
    }

    val camera = config.build()
}