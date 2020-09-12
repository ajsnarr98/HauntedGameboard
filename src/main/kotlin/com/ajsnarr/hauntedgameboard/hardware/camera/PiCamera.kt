package com.ajsnarr.hauntedgameboard.hardware.camera

import uk.co.caprica.picam.CameraConfiguration
import uk.co.caprica.picam.FilePictureCaptureHandler
import uk.co.caprica.picam.PicamNativeLibrary.installTempLibrary
import uk.co.caprica.picam.enums.Encoding
import java.io.File

open class PiCamera(config: Configuration) : Camera {

    /**
     * Picture encoding.
     */
    enum class Encoding(internal val wrappedVal: uk.co.caprica.picam.enums.Encoding) {
        BGR24(uk.co.caprica.picam.enums.Encoding.BGR24),
        BMP(uk.co.caprica.picam.enums.Encoding.BMP),
        GIF(uk.co.caprica.picam.enums.Encoding.GIF),
        I420(uk.co.caprica.picam.enums.Encoding.I420),
        JPEG(uk.co.caprica.picam.enums.Encoding.JPEG),
        OPAQUE(uk.co.caprica.picam.enums.Encoding.OPAQUE),
        PNG(uk.co.caprica.picam.enums.Encoding.PNG),
        RGB24(uk.co.caprica.picam.enums.Encoding.RGB24)
    }

    /**
     * Wraps a caprica.picam CameraConfiguration.
     *
     * There are more functions available than wrapped here.
     */
    class Configuration() {

        private val config = CameraConfiguration.cameraConfiguration()

        fun encoding(encoding: Encoding): Configuration = this.also {
            config.encoding(encoding.wrappedVal)
        }

        fun encoding(): Encoding? = Encoding.values().find { it.wrappedVal == config.encoding() }

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

    private val camera = config.build()

    fun capture(saveTo: File, captureMillis: Int? = null) {
        if (captureMillis == null) {
            camera.takePicture(FilePictureCaptureHandler(saveTo))
        } else {
            camera.takePicture(FilePictureCaptureHandler(saveTo), captureMillis)
        }
    }

    override fun close() {
        camera.close()
    }
}